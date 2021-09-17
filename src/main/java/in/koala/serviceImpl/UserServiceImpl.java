package in.koala.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.koala.domain.naverLogin.NaverCallBack;
import in.koala.domain.naverLogin.NaverToken;
import in.koala.domain.naverLogin.NaverUser;
import in.koala.mapper.UserMapper;
import in.koala.service.UserService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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

    @Value("${naver.client_id}")
    private String clientId;

    @Value("${naver.client_secret}")
    private String clientSecret;

    @Resource
    private UserMapper userMapper;

    @Override
    public String test() {
        return userMapper.test();
    }

    @Override
    public Map<String, String> naverLogin(NaverCallBack callBack) {
        String code = callBack.getCode();
        String state = callBack.getState();

        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        headers.add("Content-type", "application/x-www-form-urlencoded");
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);
        params.add("state", state);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

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

        try{
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(profile.getBody());
            ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            NaverUser naverUser = objectMapper.readValue(jsonObject.get("response").toString(), NaverUser.class);
            JSONObject response_obj = (JSONObject) jsonObject.get("response");
            String url = (String) response_obj.get("profile_image");
            //System.out.println(url);
            naverUser.setProfileImage(url);
            //System.out.println(naverUser.getId());
            //System.out.println(naverUser.getNickname());
            //System.out.println(naverUser.getProfileImage());
            //System.out.println(naverUser.getEmail());
            String findId = userMapper.findId(naverUser.getId());
            //System.out.println(findId);
            if(findId != null){
                System.out.println("회원가입 완료");
                Map<String, String > token = new HashMap<String,String>();
                token.put("access_token",  "Need to make access_token");
                token.put("refresh_token", "Need to make refresh_token");
                return token;
            }
            else{
                userMapper.signUp(naverUser);
                Map<String, String > token = new HashMap<String,String>();
                token.put("access_token",  "Need to make access_token");
                token.put("refresh_token", "Need to make refresh_token");
                return token;
            }
        }
        catch (ParseException e){
            e.printStackTrace();
            return null;
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
