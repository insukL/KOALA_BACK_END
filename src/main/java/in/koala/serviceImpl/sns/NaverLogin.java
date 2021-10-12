package in.koala.serviceImpl.sns;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.koala.domain.User;
import in.koala.domain.naverLogin.NaverUser;
import in.koala.service.SnsLoginService;
import lombok.Getter;
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
public class NaverLogin implements SnsLoginService {
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

    @Override
    public String getRedirectUri() {
        return naverLoginRequestUri +
                "&client_id=" + naverClientId +
                "&redirect_uri=" + naverRedirectUri;
    }

    @Override
    public String requestAccessToken(String code) {
            RestTemplate rt = new RestTemplate();

            ResponseEntity<String> token = rt.exchange(
                    naverAccessTokenUri,
                    HttpMethod.POST,
                    getSnsHttpEntity(code),
                    String.class
            );

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

    @Override
    public User requestUserProfile(String code) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        RestTemplate rt = new RestTemplate();

        headers.add("Authorization", "Bearer " + requestAccessToken(code));
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = rt.exchange(
                naverProfileUri,
                HttpMethod.GET,
                request,
                String.class
        );

        Map<String, String> parsedProfile = naverProfileParsing(response);

        return User.builder()
                .account(parsedProfile.get("account"))
                .sns_email(parsedProfile.get("sns_email"))
                .profile(parsedProfile.get("profile"))
                .nickname(parsedProfile.get("nickname"))
                .is_auth((short) 1)
                .build();
    }


    private HttpEntity getSnsHttpEntity(String code) {
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        headers.add("Content-type", "application/x-www-form-urlencoded");

        params.add("grant_type", "authorization_code");
        params.add("client_id", naverClientId);
        params.add("client_secret", naverClientSecret);
        params.add("code", code);

        return new HttpEntity<>(headers, params);
    }

    private Map<String, String> naverProfileParsing(ResponseEntity<String> response) throws Exception {
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

        parsedProfile.put("accout", "Naver" + "_" + naverUser.getId());
        parsedProfile.put("sns_email", naverUser.getEmail());
        parsedProfile.put("profile", naverUser.getProfile_image());
        parsedProfile.put("nickname", "Naver" + "_" + naverUser.getId());

        return parsedProfile;
    }
}