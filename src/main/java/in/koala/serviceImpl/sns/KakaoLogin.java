package in.koala.serviceImpl.sns;

import in.koala.domain.kakaoLogin.KakaoProfile;
import in.koala.service.SnsLoginService;
import lombok.Getter;
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
@Getter
public class KakaoLogin implements SnsLoginService {
    @Value("${kakao.restkey}")
    private String kakaoRestApiKey;

    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${kakao.access-token-uri}")
    private String kakaoAccessTokenUri;

    @Value("${kakao.profile-uri}")
    private String kakaoProfileUri;

    @Value("${kakao.login-request-uri}")
    private String kakaoLoginRequestUri;

    @Override
    public HttpEntity getSnsHttpEntity(String code) {
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoRestApiKey);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);

        return new HttpEntity<>(params, headers);
    }

    private Map<String, String> kakaoProfileParsing(ResponseEntity<String> response) throws Exception {

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

        parsedProfile.put("accout", "Kakao" + "_" + kakaoProfile.getId());
        parsedProfile.put("sns_email", kakaoProfile.getEmail());
        parsedProfile.put("profile", kakaoProfile.getProfile_image());
        parsedProfile.put("nickname", "Kakao" + "_" + kakaoProfile.getId());

        return parsedProfile;
    }
}
