package in.koala.util;

import com.google.auth.oauth2.GoogleCredentials;
import in.koala.domain.fcm.FcmRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.util.Arrays;

@Component
public class FcmUtil {

    @Value("${fcm.push.message.url}")
    private String fcmRequestUrl; // fcm message POST url
    @Resource
    private RestTemplate restTemplate;

    private String getAccessToken() throws Exception {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new FileInputStream("path.json")) // your admin sdk key file
                .createScoped(Arrays.asList("https://www.googleapis.com/auth/cloud-platform")); // scope

        googleCredentials.refreshIfExpired();

        return googleCredentials.getAccessToken().getTokenValue();
    }

    public void sendFcmMessage(String targetToken, String title, String body) throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + this.getAccessToken());
        FcmRequest fcmRequest = new FcmRequest(targetToken, title, body);
        HttpEntity<FcmRequest> request = new HttpEntity<FcmRequest>(fcmRequest, httpHeaders);

        restTemplate.postForObject(fcmRequestUrl, request, String.class);
    }


}

