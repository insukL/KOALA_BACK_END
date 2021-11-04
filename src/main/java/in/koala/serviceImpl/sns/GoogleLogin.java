package in.koala.serviceImpl.sns;

import in.koala.enums.SnsType;
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
public class GoogleLogin extends AbstractSnsLogin {
    @Value("${google.client-id}")
    private String clientId;

    @Value("${google.client-secret}")
    private String clientSecret;

    @Value("${google.access-token-uri}")
    private String accessTokenUri;

    @Value("${google.profile-uri}")
    private String profileUri;

    @Value("${google.redirect-uri}")
    private String redirectUri;

    @Value("${google.login-request-uri}")
    private String loginRequestUri;

    @Override
    public Map requestUserProfile(String code) throws Exception {
        return this.requestUserProfile(code, profileUri);
    }

    @Override
    public String getRedirectUri() {
        Map<String, String> map = new HashMap<>();

        map.put("client_id", clientId);
        map.put("redirect_uri", redirectUri);
        map.put("scope", "https://www.googleapis.com/auth/userinfo.email");
        map.put("response_type", "code");
        map.put("include_granted_scopes", "true");
        map.put("state", "state_parameter_passthrough_value");

        String uri = loginRequestUri;

        for(String key : map.keySet()){
            uri += "&" + key + "=" + map.get(key);
        }

        return uri;
    }

    @Override
    public SnsType getSnsType() {
        return SnsType.GOOGLE;
    }

    @Override
    public String requestAccessToken(String code) {
        return this.requestAccessToken(code, accessTokenUri);
    }

    @Override
    public Map<String, String> profileParsing(ResponseEntity<String> response) throws Exception{
        System.out.println(response);
        Map<String, String> parsedProfile = new HashMap<>();

        try{
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(response.getBody());

            parsedProfile.put("account", this.getSnsType() + "_" + (String) jsonObject.get("id"));
            parsedProfile.put("sns_email", (String) jsonObject.get("email"));
            parsedProfile.put("profile", (String) jsonObject.get("picture"));
            parsedProfile.put("nickname", this.getSnsType() + "_" + (String) jsonObject.get("id"));

        } catch (ParseException e) {
            e.printStackTrace();
            throw new Exception();
        }

        return parsedProfile;
    }

    @Override
    public HttpEntity getRequestAccessTokenHttpEntity(String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        HttpHeaders headers = new HttpHeaders();

        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        return new HttpEntity<>(params, headers);
    }
}
