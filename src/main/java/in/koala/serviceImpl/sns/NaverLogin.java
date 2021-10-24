package in.koala.serviceImpl.sns;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.koala.domain.naverLogin.NaverUser;
import in.koala.enums.ErrorMessage;
import in.koala.exception.CriticalException;
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

import java.util.HashMap;
import java.util.Map;

@Service
public class NaverLogin implements SnsLogin {
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

        String uri = loginRequestUri;

        for(String key : map.keySet()){
            uri += "&" + key + "=" + map.get(key);
        }

        return uri;
    }

    @Override
    public Map requestUserProfile(String code) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        RestTemplate rt = new RestTemplate();

        headers.add("Authorization", "Bearer " + requestAccessToken(code));
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = rt.exchange(
                profileUri,
                HttpMethod.GET,
                request,
                String.class
        );

        return  profileParsing(response);
    }

    @Override
    public HttpEntity getSnsHttpEntity(String code) {
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
    public String getSnsType() {
        return "naver";
    }

    private Map<String, String> profileParsing(ResponseEntity<String> response) throws Exception {
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

        Map<String, String> parsedProfile = new HashMap<>();

        parsedProfile.put("account", "Naver" + "_" + naverUser.getId());
        parsedProfile.put("sns_email", naverUser.getEmail());
        parsedProfile.put("profile", naverUser.getProfile_image());
        parsedProfile.put("nickname", "Naver" + "_" + naverUser.getId());

        return parsedProfile;
    }

    @Override
    public String requestAccessToken(String code) {
        RestTemplate rt = new RestTemplate();

        ResponseEntity<String> token;

        try {
            token = rt.exchange(
                    accessTokenUri,
                    HttpMethod.POST,
                    getSnsHttpEntity(code),
                    String.class
            );
        } catch(Exception e){
            throw new CriticalException(ErrorMessage.NAVER_ACCESSTOKEN_REQUEST_ERROR);
        }

        String accessToken = null;

        try{
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(token.getBody());

            accessToken = jsonObject.get("access_token").toString();

        } catch(ParseException e){
            e.printStackTrace();
        }

        return accessToken;
    }
}