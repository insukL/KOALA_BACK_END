package in.koala.serviceImpl.sns;

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
public class KakaoLogin extends AbstractSnsLogin {
    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.access-token-uri}")
    private String accessTokenUri;

    @Value("${kakao.profile-uri}")
    private String profileUri;

    @Value("${kakao.login-request-uri}")
    private String loginRequestUri;

    @Override
    public Map requestUserProfileBySnsToken(String accessToken) {
        try {
            return this.requestUserProfile(accessToken, profileUri);

        } catch(Exception e){
            throw new NonCriticalException(ErrorMessage.KAKAO_LOGIN_ERROR);
        }
    }

    @Override
    public Map requestUserProfile(String code) throws Exception {
        try {
            return this.requestUserProfile(code, profileUri);

        } catch(Exception e){
            throw new NonCriticalException(ErrorMessage.KAKAO_LOGIN_ERROR);
        }
    }

    @Override
    public String getRedirectUri() {
        Map<String, String> map = new HashMap<>();

        map.put("client_id", clientId);
        map.put("redirect_uri", redirectUri);
        map.put("response_type", "code");

        String uri = loginRequestUri;

        for(String key : map.keySet()){
            uri += "&" + key + "=" + map.get(key);
        }

        return uri;
    }

    @Override
    public SnsType getSnsType() {
        return SnsType.KAKAO;
    }

    @Override
    public Map<String, String> profileParsing(ResponseEntity<String> response) throws Exception {

        Map<String, String> parsedProfile = new HashMap<>();

        try{
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(response.getBody());
            JSONObject kakaoAccount = (JSONObject) jsonObject.get("kakao_account");
            JSONObject profile = (JSONObject) kakaoAccount.get("profile");

            parsedProfile.put("account", this.getSnsType() + "_" + ((Long) jsonObject.get("id")).toString());
            parsedProfile.put("sns_email", (String) kakaoAccount.get("email"));
            parsedProfile.put("profile", (String) profile.get("profile_image_url"));
            parsedProfile.put("nickname", this.getSnsType() + "_" + ((Long) jsonObject.get("id")).toString());
            parsedProfile.put("user_type", "3");

        } catch (ParseException e) {
            e.printStackTrace();
            throw new Exception();
        }

        return parsedProfile;
    }

    @Override
    public HttpEntity getRequestAccessTokenHttpEntity(String code) {
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        return new HttpEntity<>(params, headers);
    }

    @Override
    public String requestAccessToken(String code) {
        try {
            return this.requestAccessToken(code, accessTokenUri);

        } catch (Exception e) {
            throw new NonCriticalException(ErrorMessage.KAKAO_LOGIN_ERROR);
        }
    }
}
