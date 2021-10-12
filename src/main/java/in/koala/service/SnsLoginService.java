package in.koala.service;

import in.koala.domain.User;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;


public interface SnsLoginService {
    Map requestUserProfile(String code) throws Exception;
    String getRedirectUri();
    HttpEntity getSnsHttpEntity(String code);

    // sns 사에 access token 요청하는 메서드
    default String requestAccessToken(String code, String uri) {
        RestTemplate rt = new RestTemplate();

        ResponseEntity<String> token = rt.exchange(
                uri,
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
