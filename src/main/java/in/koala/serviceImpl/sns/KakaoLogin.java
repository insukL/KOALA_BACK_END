package in.koala.serviceImpl.sns;

import in.koala.domain.kakaoLogin.KakaoProfile;
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
public class KakaoLogin implements SnsLogin {
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
    public Map requestUserProfile(String code) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        RestTemplate rt = new RestTemplate();

        headers.add("Authorization", "Bearer " + requestAccessToken(code, accessTokenUri));
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

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
        System.out.println(uri);
        return uri;
    }

    @Override
    public String getSnsType() {
        return "kakao";
    }

    private Map<String, String> profileParsing(ResponseEntity<String> response) throws Exception {

        KakaoProfile kakaoProfile = null;

        try{
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(response.getBody());
            JSONObject kakaoAccount = (JSONObject) jsonObject.get("kakao_account");
            JSONObject profile = (JSONObject) kakaoAccount.get("profile");

            kakaoProfile = new KakaoProfile().builder()
                    .nickname((String) profile.get("nickname"))
                    .profile_image((String) profile.get("profile_image_url"))
                    .id(((Long) jsonObject.get("id")).toString())
                    .email((String) kakaoAccount.get("email"))
                    .build();

        } catch (ParseException e) {
            e.printStackTrace();
            throw new Exception();
        }

        Map<String, String> parsedProfile = new HashMap<>();

        parsedProfile.put("account", "Kakao" + "_" + kakaoProfile.getId());
        parsedProfile.put("sns_email", kakaoProfile.getEmail());
        parsedProfile.put("profile", kakaoProfile.getProfile_image());
        parsedProfile.put("nickname", "Kakao" + "_" + kakaoProfile.getId());

        return parsedProfile;
    }

    @Override
    public HttpEntity getSnsHttpEntity(String code) {
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        return new HttpEntity<>(params, headers);
    }
}
