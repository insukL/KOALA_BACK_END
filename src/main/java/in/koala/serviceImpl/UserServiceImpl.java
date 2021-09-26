package in.koala.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.koala.domain.User;
import in.koala.domain.kakaoLogin.KakaoProfile;
import in.koala.domain.naverLogin.NaverCallBack;
import in.koala.domain.naverLogin.NaverToken;
import in.koala.domain.naverLogin.NaverUser;
import in.koala.mapper.UserMapper;
import in.koala.service.UserService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Value("${naver.client_id}")
    private String naverClientId;

    @Value("${naver.client_secret}")
    private String naverClientSecret;

    @Value("${naver.access-token-uri}")
    private String naverAccessTokenUri;

    @Value("${naver.profile-uri}")
    private String naverProfileUri;

    @Value("${kakao.restkey}")
    private String kakaoRestApiKey;

    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${kakao.access-token-uri}")
    private String kakaoAccessTokenUri;

    @Value("${kakao.profile-uri}")
    private String kakaoProfileUri;

    @Autowired
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public String test() {
        return userMapper.test();
    }

    @Override
    public Map<String, String> snsLogin(String code, String snsType) throws Exception {

        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        String accessTokenUri = null;
        String profileUri = null;

        if(snsType == "Naver"){
            headers.add("Content-type", "application/x-www-form-urlencoded");

            params.add("grant_type", "authorization_code");
            params.add("client_id", naverClientId);
            params.add("client_secret", naverClientSecret);
            params.add("code", code);

            accessTokenUri = naverAccessTokenUri;
            profileUri = naverProfileUri;

        } else if(snsType == "Kakao"){
            headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            params.add("grant_type", "authorization_code");
            params.add("client_id", kakaoRestApiKey);
            params.add("redirect_uri", kakaoRedirectUri);
            params.add("code", code);

            accessTokenUri = kakaoAccessTokenUri;
            profileUri = kakaoProfileUri;
        }

        String accessToken = getAccessToken(new HttpEntity<>(params, headers), accessTokenUri);

        headers.clear();
        headers.add("Authorization", "Bearer " + accessToken);

        User user = getUserProfile(new HttpEntity<>(headers), profileUri, snsType);

        Long id = userMapper.getIdByAccount(user.getAccount());

        if(id == null) userMapper.signUp(user);

        return generateToken(id);
    }

    @Override
    public User signUp(User user) {
        User selectUser = userMapper.getUserByAccount(user.getAccount());

        if(selectUser != null);

        if(userMapper.checkNickname(user.getNickname()) >= 1);

        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        userMapper.signUp(user);

        return userMapper.getUserById(user.getId());
    }

    @Override
    public Map<String, String> login(User user) {
        return null;
    }

    private Map generateToken(Long id){
        Map<String, String> token = new HashMap<>();

        token.put("access_token",  "Need to make access_token");
        token.put("refresh_token", "Need to make refresh_token");

        return token;
    }

    // 각 sns 서비스에서 access_token 을 요청, 반환받은 json 객체에서 access_token 을 파싱하여 반환
    private String getAccessToken(HttpEntity<MultiValueMap<String, String>> request, String uri){
        RestTemplate rt = new RestTemplate();

        ResponseEntity<String> token = rt.exchange(
                uri,
                HttpMethod.POST,
                request,
                String.class
        );

        //System.out.println(token);

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
    private User getUserProfile(HttpEntity<MultiValueMap<String, String>> request, String uri, String snsType) throws Exception {
        RestTemplate rt = new RestTemplate();

        ResponseEntity<String> response = rt.exchange(
                uri,
                HttpMethod.POST,
                request,
                String.class
        );

        //System.out.println(response);

        User user = null;

        if(snsType == "Naver"){
            user = naverProfileParsing(response);

        } else if(snsType == "Kakao"){
            user = kakaoProfileParsing(response);
        }

        return user;
    }

    private User kakaoProfileParsing(ResponseEntity<String> response) throws Exception {

        KakaoProfile kakaoProfile = new KakaoProfile();

        try{
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(response.getBody());
            JSONObject kakaoAccount = (JSONObject) jsonObject.get("kakao_account");
            JSONObject profile = (JSONObject) kakaoAccount.get("profile");

            kakaoProfile.setNickname((String) profile.get("nickname"));
            kakaoProfile.setProfile_image((String) profile.get("profile_image_url"));
            kakaoProfile.setId(((Long) jsonObject.get("id")).toString());
            kakaoProfile.setEmail((String) kakaoAccount.get("email"));

        } catch (ParseException e) {
            e.printStackTrace();
            throw new Exception();
        }

        User user = new User();
        user.setAccount("Kakao" + "_" + kakaoProfile.getId());
        user.setNickname(kakaoProfile.getEmail() + "_" + kakaoProfile.getId());
        user.setSns_email(kakaoProfile.getEmail());
        user.setPassword(kakaoProfile.getProfile_image());
        user.setIs_auth((short) 1);

        return user;
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
            naverUser.setProfileImage(url);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new Exception();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new Exception();
        }

        User user = new User();
        user.setAccount("Naver" + "_" + naverUser.getId());
        user.setSns_email(naverUser.getEmail());
        user.setProfile(naverUser.getProfileImage());
        user.setIs_auth((short) 1);
        user.setNickname(naverUser.getEmail() + "_" + naverUser.getId());

        return user;
    }
}
