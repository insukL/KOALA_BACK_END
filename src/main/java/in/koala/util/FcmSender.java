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
    public void sendMessage(FcmMessage message) throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + this.getAccessToken());
        httpHeaders.add("Content-Type", "application/json; charset=UTF-8");
        FcmRequest fcmRequest = new FcmRequest(message);
        HttpEntity<FcmRequest> request = new HttpEntity<FcmRequest>(fcmRequest, httpHeaders);
        restTemplate.postForObject(fcmRequestUrl, request, String.class);
    }

    // 토큰을 유지 및 자바 객체로 전송
    public void register(String token, String name){
        tokens.put(name, token);
    }

    public void cancel(String name){
        tokens.remove(name);
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

}
