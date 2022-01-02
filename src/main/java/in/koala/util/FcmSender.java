package in.koala.util;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.messaging.*;
import in.koala.domain.fcm.FcmMessage;
import in.koala.domain.fcm.FcmRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.util.*;

@Component
public class FcmSender {
    //어노테이션 Resource와 이름이 같아서 아래와 같이 패키지 경로까지 명시됨
    @Value("${firebase.key.path}")
    private org.springframework.core.io.Resource key;

    @Value("${fcmRequestUrl}")
    private String fcmRequestUrl;

    @Resource
    RestTemplate restTemplate;

    private String getAccessToken() throws Exception {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new FileInputStream(key.getFile()))
                .createScoped(Arrays.asList("https://www.googleapis.com/auth/firebase.messaging"));
        googleCredentials.refreshIfExpired();

        return googleCredentials.getAccessToken().getTokenValue();
    }

    //Http 메시지 요청
    public void sendMessage(FcmMessage message) throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + this.getAccessToken());
        httpHeaders.add("Content-Type", "application/json; charset=UTF-8");
        FcmRequest fcmRequest = new FcmRequest(message);
        HttpEntity<FcmRequest> request = new HttpEntity<FcmRequest>(fcmRequest, httpHeaders);
        restTemplate.postForObject(fcmRequestUrl, request, String.class);
    }

    //주제 구독
    public void subscribe(List<String> tokens, String topic) throws Exception{
        TopicManagementResponse response = FirebaseMessaging
                .getInstance().subscribeToTopic(tokens, topic);
        System.out.println(response.getSuccessCount() + "회 성공했습니다.");
    }

    //주제 구독 취소
    public void unsubscribe(List<String> tokens, String topic) throws Exception{
        TopicManagementResponse response = FirebaseMessaging
                .getInstance().unsubscribeFromTopic(tokens, topic);
        System.out.println(response.getSuccessCount() + "회 성공했습니다.");
    }

    //복수 주제 대상
    public void subscribe(List<String> tokens, List<String> topic) throws Exception{
        for(String t : topic) {
            TopicManagementResponse response = FirebaseMessaging
                    .getInstance().subscribeToTopic(tokens, t);
            System.out.println(response.getSuccessCount() + "회 성공했습니다.");
        }
    }

    //주제 구독 취소
    public void unsubscribe(List<String> tokens, List<String> topic) throws Exception{
        for(String t : topic) {
            TopicManagementResponse response = FirebaseMessaging
                    .getInstance().unsubscribeFromTopic(tokens, t);
            System.out.println(response.getSuccessCount() + "회 성공했습니다.");
        }
    }

}
