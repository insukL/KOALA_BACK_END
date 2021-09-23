package in.koala.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.koala.domain.User;
import in.koala.domain.naverLogin.NaverCallBack;
import in.koala.domain.naverLogin.NaverToken;
import in.koala.domain.naverLogin.NaverUser;
import in.koala.mapper.UserMapper;
import in.koala.service.UserService;
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
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Value("${naver.client_id}")
    private String clientId;

    @Value("${naver.client_secret}")
    private String clientSecret;

    @Override
    public String test() {
        return userMapper.test();
    }

    public Map snsLogin(String code, String snsType){
        String accessToken;

        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        if(snsType == "Naver"){
            headers.add("Content-type", "application/x-www-form-urlencoded");

            params.add("grant_type", "authorization_code");
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("code", code);

            accessToken = getAccessToken(new HttpEntity<>(params, headers), "https://nid.naver.com/oauth2.0/token");
        }
        else if(snsType == "Kakao"){
            headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            params.add("grant_type", "authorization_code");
            params.add("client_id", kakaoRestApiKey);
            params.add("redirect_uri", kakaoRedirectUri);
            params.add("code", code);

            accessToken = getAccessToken(new HttpEntity<>(params, headers), "https://kauth.kakao.com/oauth/token");
        }
    }

    private String getAccessToken(HttpEntity<MultiValueMap<String, String>> request, String url){
        RestTemplate rt = new RestTemplate();

        ResponseEntity<String> token = rt.exchange(
                url,
                HttpMethod.POST,
                request,
                String.class
        );

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

    @Override
    public Map<String, String> naverLogin(NaverCallBack callBack) {
        String code = callBack.getCode();

        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        headers.add("Content-type", "application/x-www-form-urlencoded");

        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        NaverToken naverToke = new NaverToken();
        ResponseEntity<NaverToken> naverToken = rt.exchange(
                "https://nid.naver.com/oauth2.0/token",
                HttpMethod.POST,
                request,
                NaverToken.class
        );

        String accessToken = naverToken.getBody().getAccess_token();

        headers.clear();
        headers.add("Authorization", "Bearer " + accessToken);
        HttpEntity<HttpHeaders> profileHttpEntity = new HttpEntity<>(headers);
        ResponseEntity<String> profile = rt.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.POST,
                profileHttpEntity,
                String.class
        );

        NaverUser naverUser = null;

        try{
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(profile.getBody());
            ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            naverUser = objectMapper.readValue(jsonObject.get("response").toString(), NaverUser.class);
            JSONObject response_obj = (JSONObject) jsonObject.get("response");
            String url = (String) response_obj.get("profile_image");
            naverUser.setProfileImage(url);
        }
        catch (ParseException e){
            e.printStackTrace();
            return null;
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }

        Long id = userMapper.getIdBySnsEmail(naverUser.getEmail());

        if(id == null) {
            User user = new User();
            user.setSns_email(naverUser.getEmail());
            user.setProfile(naverUser.getProfileImage());
            user.setIs_auth((short) 1);
            user.setNickname(naverUser.getEmail() + "_" + naverUser.getId());
            userMapper.snsSingUp(user);
        }

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
}
