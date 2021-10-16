package in.koala.serviceImpl.sns;

import in.koala.domain.googleLogin.GoogleProfile;
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
public class GoogleLogin implements SnsLogin {
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
        HttpHeaders headers = new HttpHeaders();
        RestTemplate rt = new RestTemplate();
        System.out.println("11111");
        headers.add("Authorization", "Bearer " + requestAccessToken(code, accessTokenUri));
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        System.out.println("22222");
        ResponseEntity<String> response = rt.exchange(
                profileUri,
                HttpMethod.GET,
                request,
                String.class
        );

        return profileParsing(response);
    }

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

    private Map<String, String> profileParsing(ResponseEntity<String> response) throws Exception{
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

        Map<String, String> parsedProfile = new HashMap<>();

        parsedProfile.put("account", "Google" + "_" + googleProfile.getId());
        parsedProfile.put("sns_email", googleProfile.getEmail());
        parsedProfile.put("profile", googleProfile.getProfile_image());
        parsedProfile.put("nickname", "Google" + "_" + googleProfile.getId());

        return parsedProfile;
    }

    @Override
    public HttpEntity getSnsHttpEntity(String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        return new HttpEntity<>(params);
    }

    @Override
    public String getSnsType() {
        return "google";
    }
}
