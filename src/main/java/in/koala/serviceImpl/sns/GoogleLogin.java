package in.koala.serviceImpl.sns;

import in.koala.domain.googleLogin.GoogleProfile;
import in.koala.service.SnsLoginService;
import lombok.Getter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

@Service
@Getter
public class GoogleLogin implements SnsLoginService {
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

    @Override
    public HttpEntity getSnsHttpEntity(String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("code", code);
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("redirect_uri", googleRedirectUri);
        params.add("grant_type", "authorization_code");

        return new HttpEntity<>(params);
    }

    private Map<String, String> googleProfileParsing(ResponseEntity<String> response) throws Exception{
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

        parsedProfile.put("accout", "Google" + "_" + googleProfile.getId());
        parsedProfile.put("sns_email", googleProfile.getEmail());
        parsedProfile.put("profile", googleProfile.getProfile_image());
        parsedProfile.put("nickname", "Google" + "_" + googleProfile.getId());

        return parsedProfile;
    }
}
