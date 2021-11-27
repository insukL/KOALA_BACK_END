package in.koala.util;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.messaging.*;
import in.koala.domain.Fcm.FcmRequest;
import in.koala.domain.Fcm.TokenMessage;
import in.koala.domain.Fcm.TopicMessage;
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
    @Value("${firebase.key_path}")
    private String keyPath;

    @Value("${fcmRequestUrl}")
    private String fcmRequestUrl;

    @Resource
    RestTemplate restTemplate;

    Map<String, String> tokens = new HashMap<String, String>();

    private String getAccessToken() throws Exception {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new FileInputStream(keyPath))
                .createScoped(Arrays.asList("https://www.googleapis.com/auth/firebase.messaging"));
        googleCredentials.refreshIfExpired();

        return googleCredentials.getAccessToken().getTokenValue();
    }

    //Http 메시지 요청
    public void sendMessage(String targetToken, String title, String body) throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + this.getAccessToken());
        TokenMessage message = new TokenMessage(title, body, targetToken);
        FcmRequest fcmRequest = new FcmRequest(message);
        HttpEntity<FcmRequest> request = new HttpEntity<FcmRequest>(fcmRequest, httpHeaders);
        restTemplate.postForObject(fcmRequestUrl, request, String.class);
        System.out.println("전송 완료");
    }

    //자바 객체 요청
    public void sendMessageV2(String targetToken, String title, String body) throws FirebaseMessagingException {
        Message message = Message.builder()
                .setToken(targetToken)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();
        String response = FirebaseMessaging.getInstance().send(message);
        System.out.println("전송 완료 : " + response);
    }

    // 토큰을 유지 및 자바 객체로 전송
    public void register(String token, String name){
        tokens.put(name, token);
    }

    public void cancel(String name){
        tokens.remove(name);
    }

    //자바 객체 멀티 메시지
    public void sendMultiToken(String title, String body) throws FirebaseMessagingException {
        MulticastMessage message = MulticastMessage.builder()
                .addAllTokens(tokens.values())
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();
        BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
        System.out.println("전송 완료 : " + response);
    }
    
    //주제 구독
    public void subscribe(List<String> names, String topic) throws Exception{
        List<String> targetTokens = new ArrayList<String>();
        for(String n : names){
            targetTokens.add(tokens.get(n));
        }
        if(targetTokens == null){
            throw new Exception("token이 없습니다.");
        }
        TopicManagementResponse response = FirebaseMessaging
                .getInstance().subscribeToTopic(targetTokens, topic);
        System.out.println(response.getSuccessCount() + "회 성공했습니다.");
    }

    //주제 구독 취소
    public void unsubscribe(List<String> names, String topic) throws Exception{
        List<String> targetTokens = new ArrayList<String>();
        for(String n : names){
            targetTokens.add(tokens.get(n));
        }
        if(targetTokens == null){
            throw new Exception("token이 없습니다.");
        }
        TopicManagementResponse response = FirebaseMessaging
                .getInstance().unsubscribeFromTopic(targetTokens, topic);
        System.out.println(response.getSuccessCount() + "회 성공했습니다.");
    }

    //주제 전송(Http)
    public void sendMessageTopic(String topic, String title, String body) throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + this.getAccessToken());
        TopicMessage message = new TopicMessage(title, body, topic);
        FcmRequest fcmRequest = new FcmRequest(message);
        HttpEntity<FcmRequest> request = new HttpEntity<FcmRequest>(fcmRequest, httpHeaders);
        restTemplate.postForObject(fcmRequestUrl, request, String.class);
        System.out.println("전송 완료");
    }

    //주제 전송(자바 객체)
    public void sendMessageTopicV2(String topic, String title, String body) throws FirebaseMessagingException {
        Message message = Message.builder()
                .setTopic(topic)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();
        String response = FirebaseMessaging.getInstance().send(message);
        System.out.println("전송 완료 : " + response);
    }
}
