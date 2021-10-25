package in.koala.service.sns;

import in.koala.domain.User;
import in.koala.enums.ErrorMessage;
import in.koala.enums.SnsType;
import in.koala.exception.CriticalException;
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

// 구현이 똑같은 메서드가 있는데 이것을 default 메서드로 할지 고민
public interface SnsLogin {
    Map requestUserProfile(String code) throws Exception;
    Map profileParsing(ResponseEntity<String> response) throws Exception;
    String getRedirectUri();
    HttpEntity getRequestAccessTokenHttpEntity(String code);
    SnsType getSnsType();
    // sns 사에 access token 요청하는 메서드
    String requestAccessToken(String code);

}
