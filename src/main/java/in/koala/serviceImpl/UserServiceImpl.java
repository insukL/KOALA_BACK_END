package in.koala.serviceImpl;

import com.amazonaws.services.dynamodbv2.xspec.S;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.koala.domain.User;
import in.koala.domain.googleLogin.GoogleProfile;
import in.koala.domain.kakaoLogin.KakaoProfile;
import in.koala.domain.naverLogin.NaverUser;
import in.koala.enums.ErrorMessage;
import in.koala.exception.NonCriticalException;
import in.koala.mapper.UserMapper;
import in.koala.service.UserService;
import in.koala.util.Jwt;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final HttpServletResponse response;
    private final Jwt jwt;

    @Value("${naver.client_id}")
    private String naverClientId;

    @Value("${naver.client_secret}")
    private String naverClientSecret;

    @Value("${naver.access-token-uri}")
    private String naverAccessTokenUri;

    @Value("${naver.profile-uri}")
    private String naverProfileUri;

    @Value("${naver.redirect-uri}")
    private String naverRedirectUri;

    @Value("${naver.login-request-uri}")
    private String naverLoginRequestUri;

    @Value("${kakao.restkey}")
    private String kakaoRestApiKey;

    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${kakao.access-token-uri}")
    private String kakaoAccessTokenUri;

    @Value("${kakao.profile-uri}")
    private String kakaoProfileUri;

    @Value("${kakao.login-request-uri}")
    private String kakaoLoginRequestUri;

    @Value("${google.client-id}")
    private String googleClientId;

    @Value("${google.client-secret}")
    private String googleClientSecret;

    @Value("${google.access-token-uri}")
    private String googleAccessTokenUri;

    @Value("${google.profile-uri}")
    private String googleProfileUri;

    @Value("${google.redirect-uri}")
    private String googleRedirectUri;

    @Value("${google.login-request-uri}")
    private String googleLoginRequestUri;

    @Value("${spring.jwt.access-token}")
    private String accessToken;

    @Value("${spring.jwt.refresh-token}")
    private String refreshToken;

    @Override
    public String test() {
        return userMapper.test();
    }

    // snstype을 열거형으로 바꾸기
    // snstype에 따라 달라지는 코드들은 따로 sns 별로 인터페이스 생성 후 구현하는 클래스 만들기
    // oauth를 user와 분리하여 따로 controller와 service 만드는 것도 고려
    @Override
    public Map<String, String> snsLogin(String code, String snsType) throws Exception {

        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        String accessTokenUri = null;
        String profileUri = null;

        //sns별 헤더 생성
        if(snsType.equals("naver")){
            headers.add("Content-type", "application/x-www-form-urlencoded");

            params.add("grant_type", "authorization_code");
            params.add("client_id", naverClientId);
            params.add("client_secret", naverClientSecret);
            params.add("code", code);

            accessTokenUri = naverAccessTokenUri;
            profileUri = naverProfileUri;

        } else if(snsType.equals("kakao")){
            headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            params.add("grant_type", "authorization_code");
            params.add("client_id", kakaoRestApiKey);
            params.add("redirect_uri", kakaoRedirectUri);
            params.add("code", code);

            accessTokenUri = kakaoAccessTokenUri;
            profileUri = kakaoProfileUri;

        } else if(snsType.equals("google")){
            params.add("code", code);
            params.add("client_id", googleClientId);
            params.add("client_secret", googleClientSecret);
            params.add("redirect_uri", googleRedirectUri);
            params.add("grant_type", "authorization_code");

            accessTokenUri = googleAccessTokenUri;
            profileUri = googleProfileUri;
        }

        // 각 oauth2 제공하는 서비스 회사에 accessToken 요청
        String accessToken = requestAccessToken(new HttpEntity<>(params, headers), accessTokenUri);


        headers.clear();
        headers.add("Authorization", "Bearer " + accessToken);

        // accessToken 을 이용하여 각 oauth2 제공하는 서비스 회사에 유저정보 요청
        User user = requestUserProfile(new HttpEntity<>(headers), profileUri, snsType);

        Long id = userMapper.getIdByAccount(user.getAccount());

        // 해당 유저가 처음 sns 로그인을 요청한다면 회원가입
        if(id == null) userMapper.snsSignUp(user);

        return generateToken(id);
    }

    // sns 별 oauth2 로그인 요청을 하는 메서드, 해당 api 요청한 페이지를 redirect 시킨다.
    @Override
    public void requestSnsLogin(String snsType) throws Exception {
        String uri = null;

        if(snsType.equals("naver")) {
            uri = naverLoginRequestUri +
                    "&client_id=" + naverClientId +
                    "&redirect_uri=" + naverRedirectUri;

        } else if(snsType.equals("kakao")){
            uri = kakaoLoginRequestUri +
                    "&client_id=" + kakaoRestApiKey +
                    "&redirect_uri=" + kakaoRedirectUri;

        } else if(snsType.equals("google")){
            uri = googleLoginRequestUri +
                    "&client_id=" + googleClientId +
                    "&redirect_uri=" + googleRedirectUri;
        }

        //System.out.println(uri);
        response.sendRedirect(uri);
    }

    @Override
    public User signUp(User user) {
        User selectUser = userMapper.getUserByAccount(user.getAccount());

        // 해당 계정명이 이미 존재한다면 예외처리
        if(selectUser != null) throw new NonCriticalException(ErrorMessage.ACCOUNT_ALREADY_EXIST);

        // 해당 닉네임이 이미 존재한다면 예외처리
        if(userMapper.checkNickname(user.getNickname()) >= 1) throw new NonCriticalException(ErrorMessage.NICKNAME_ALREADY_EXIST);

        // 비밀번호 단방향 암호화
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        
        userMapper.signUp(user);

        return userMapper.getUserById(user.getId());
    }

    @Override
    public Map<String, String> login(User user) {
        User loginUser = userMapper.getUserByAccount(user.getAccount());

        // 해당 계정이 존재하지 않는다면 예외처리
        if(loginUser == null) throw new NonCriticalException(ErrorMessage.ACCOUNT_NOT_EXIST);

        // 계정은 존재하나 비밀번호가 존재하지 않는다면 예외처리
        if(!BCrypt.checkpw(user.getPassword(), loginUser.getPassword())) throw new NonCriticalException(ErrorMessage.WRONG_PASSWORD_EXCEPTION);

        return generateToken(loginUser.getId());
    }

    @Override
    public User getMyInfo() {
        Long id = getLoginUserId();

        User user = userMapper.getUserById(id);

        if(user == null) throw new NonCriticalException(ErrorMessage.USER_NOT_EXIST);

        return user;
    }


    private Long getLoginUserId(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization");

        return null;
    }

    private Map<String, String> generateToken(Long id){
        Map<String, String> token = new HashMap<>();

        token.put("access_token", jwt.generateToken(id, accessToken));
        token.put("refresh_token", jwt.generateToken(id, refreshToken));

        return token;
    }

    // 각 sns 서비스에서 access_token 을 요청, 반환받은 json 객체에서 access_token 을 파싱하여 반환
    private String requestAccessToken(HttpEntity<MultiValueMap<String, String>> request, String uri){
        RestTemplate rt = new RestTemplate();

        ResponseEntity<String> token = rt.exchange(
                uri,
                HttpMethod.POST,
                request,
                String.class
        );

        System.out.println(token);

        String access_token = null;

        try{
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(token.getBody());

            access_token = jsonObject.get("access_token").toString();

        } catch(ParseException e){
            e.printStackTrace();
        }

        return access_token;
    }

    // 유저 프로파일을 sns 서비스에 요청, 반환받은 json 객체를 sns 서비스에 맞게 파싱하여 User 객체 생성 후 반환
    private User requestUserProfile(HttpEntity<MultiValueMap<String, String>> request, String uri, String snsType) throws Exception {
        RestTemplate rt = new RestTemplate();

        ResponseEntity<String> response = rt.exchange(
                uri,
                HttpMethod.GET,
                request,
                String.class
        );

        System.out.println(response);

        User user = null;

        if(snsType.equals("naver")){
            user = naverProfileParsing(response);

        } else if(snsType.equals("kakao")){
            user = kakaoProfileParsing(response);

        } else if(snsType.equals("google")){
            user = googleProfileParsing(response);
        }

        return user;
    }

    private User googleProfileParsing(ResponseEntity<String> response) throws Exception{
        GoogleProfile googleProfile = null;

        try{
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(response.getBody());

            googleProfile = new GoogleProfile().builder()
                    .nickname((String) jsonObject.get("name"))
                    .profile_image((String) jsonObject.get("picture"))
                    .id((String) jsonObject.get("id"))
                    .email((String) jsonObject.get("email"))
                    .build();

        } catch (ParseException e) {
            e.printStackTrace();
            throw new Exception();
        }

        return new User().builder()
                .account("Google" + "_" + googleProfile.getId())
                .sns_email(googleProfile.getEmail())
                .profile(googleProfile.getProfile_image())
                .is_auth((short) 1)
                .nickname("Google" + "_" + googleProfile.getId())
                .build();
    }

    private User kakaoProfileParsing(ResponseEntity<String> response) throws Exception {

        KakaoProfile kakaoProfile = null;

        try{
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(response.getBody());
            JSONObject kakaoAccount = (JSONObject) jsonObject.get("kakao_account");
            JSONObject profile = (JSONObject) kakaoAccount.get("profile");

            kakaoProfile = new KakaoProfile().builder()
                    .nickname((String) profile.get("nickname"))
                    .profile_image((String) profile.get("profile_image_url"))
                    .id(((Long) jsonObject.get("id")).toString())
                    .email((String) kakaoAccount.get("email"))
                    .build();

        } catch (ParseException e) {
            e.printStackTrace();
            throw new Exception();
        }

        return new User().builder()
                .account("Kakao" + "_" + kakaoProfile.getId())
                .sns_email(kakaoProfile.getEmail())
                .profile(kakaoProfile.getProfile_image())
                .is_auth((short) 1)
                .nickname("Kakao" + "_" + kakaoProfile.getId())
                .build();
    }

    private User naverProfileParsing(ResponseEntity<String> response) throws Exception {
        NaverUser naverUser = null;

        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(response.getBody());
            ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            naverUser = objectMapper.readValue(jsonObject.get("response").toString(), NaverUser.class);
            JSONObject response_obj = (JSONObject) jsonObject.get("response");
            String url = (String) response_obj.get("profile_image");
            naverUser.setProfile_image(url);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new Exception();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new Exception();
        }

        return new User().builder()
                .account("Naver" + "_" + naverUser.getId())
                .sns_email(naverUser.getEmail())
                .profile(naverUser.getProfile_image())
                .is_auth((short) 1)
                .nickname("Naver" + "_" + naverUser.getId())
                .build();
    }
}
