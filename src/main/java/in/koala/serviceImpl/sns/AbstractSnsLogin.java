package in.koala.serviceImpl.sns;

import in.koala.enums.ErrorMessage;
import in.koala.exception.NonCriticalException;
import in.koala.service.sns.SnsLogin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Map;

// 인터페이스 구현의 중복을 제거하기 위해 생성한 SnsLogin 인터페이스를 상속받는 abstract 클래스
public abstract class AbstractSnsLogin implements SnsLogin {

    protected Map requestUserProfile(String code, String profileUri) throws Exception {
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

        Map parsedProfile = this.profileParsing(response);

        if(parsedProfile.get("account") == null || parsedProfile.get("sns_email") == null || parsedProfile.get("profile") == null || parsedProfile.get("nickname") == null){
            throw new NonCriticalException(ErrorMessage.PROFILE_SCOPE_ERROR);
        }

        return parsedProfile;
    }

    protected String requestAccessToken(String code, String accessTokenUri) {
        RestTemplate rt = new RestTemplate();

        ResponseEntity<String> token;

        token = rt.exchange(
                accessTokenUri,
                HttpMethod.POST,
                this.getRequestAccessTokenHttpEntity(code),
                String.class
        );

        String accessToken = null;

        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(token.getBody());

            accessToken = jsonObject.get("access_token").toString();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return accessToken;
    }

    abstract Map profileParsing(ResponseEntity<String> response) throws Exception;
    abstract HttpEntity getRequestAccessTokenHttpEntity(String code);
}
