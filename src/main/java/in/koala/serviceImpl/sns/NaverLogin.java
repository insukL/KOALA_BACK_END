package in.koala.serviceImpl.sns;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.koala.domain.sns.SnsUser;
import in.koala.domain.sns.naverLogin.NaverUser;
import in.koala.enums.ErrorMessage;
import in.koala.enums.SnsType;
import in.koala.exception.NonCriticalException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

@Service
public class NaverLogin extends AccessTokenSnsLogin {

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client_secret}")
    private String clientSecret;

    @Value("${naver.access-token-uri}")
    private String accessTokenUri;

    @Value("${naver.profile-uri}")
    private String profileUri;

    @Value("${naver.redirect-uri}")
    private String redirectUri;

    @Value("${naver.login-request-uri}")
    private String loginRequestUri;

    @Override
    public String getRedirectUri() {
        Map<String, String> map = new HashMap<>();

        map.put("client_id", clientId);
        map.put("redirect_uri", redirectUri);
        map.put("response_type", "code");
        map.put("state", "STATE_STRING");

        String uri = loginRequestUri;

        for(String key : map.keySet()){
            uri += "&" + key + "=" + map.get(key);
        }

        return uri;
    }

    @Override
    public SnsUser requestUserProfile(String code) throws Exception {
        try {
            return this.requestUserProfile(code, profileUri);

        } catch(Exception e){
            throw new NonCriticalException(ErrorMessage.NAVER_LOGIN_ERROR);
        }
    }

    @Override
    public SnsUser requestUserProfileByToken(String token) {
        try {
            return this.requestUserProfileByAccessToken(token, profileUri);

        } catch(Exception e){
            e.printStackTrace();
            throw new NonCriticalException(ErrorMessage.NAVER_LOGIN_ERROR);
        }
    }

    @Override
    public HttpEntity getRequestAccessTokenHttpEntity(String code) {
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        headers.add("Content-type", "application/x-www-form-urlencoded");

        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);

        return new HttpEntity<>(params, headers);
    }

    @Override
    public SnsType getSnsType() {
        return SnsType.NAVER;
    }

    @Override
    public SnsUser profileParsing(ResponseEntity<String> response) throws Exception {
        NaverUser naverUser;

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

        return SnsUser.builder()
                .account(this.getSnsType() + "_" + naverUser.getId())
                .email(naverUser.getEmail())
                .profile(naverUser.getProfile_image())
                .nickname(this.getSnsType() + "_" + naverUser.getId())
                .snsType(SnsType.NAVER)
                .build();
    }

    @Override
    public String requestAccessToken(String code) {
        try {
            return this.requestAccessToken(code, accessTokenUri);

        } catch (Exception e) {
            throw new NonCriticalException(ErrorMessage.NAVER_LOGIN_ERROR);
        }
    }
}