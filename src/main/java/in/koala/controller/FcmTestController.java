package in.koala.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import in.koala.domain.Fcm.*;
import in.koala.service.FcmTestService;
import in.koala.util.FcmSender;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;

@RestController
@RequestMapping("/fcm")
public class FcmTestController {
    @Resource
    FcmTestService fcmTestService;

    @Resource
    FcmSender fcmSender;

    @GetMapping("/http")
    public ResponseEntity sendTest(@RequestParam String token) {
        try {
            return new ResponseEntity(fcmTestService.sendTest(token), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity("fail", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/java")
    public ResponseEntity sendJavaTest(@RequestParam String token){
        try {
            return new ResponseEntity(fcmTestService.sendJavaTest(token), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity("fail", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/sub")
    public ResponseEntity subscribe(@RequestParam String name,
                                    @RequestParam String topic){
        try {
            fcmSender.subscribe(Arrays.asList(name), topic);
            return new ResponseEntity("success", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity("fail", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/unsub")
    public ResponseEntity unsubscribe(@RequestParam String name,
                                      @RequestParam String topic){
        try {
            fcmSender.unsubscribe(Arrays.asList(name), topic);
            return new ResponseEntity("success", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity("fail", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/topic")
    public ResponseEntity topicTest(@RequestParam String topic){
        try {
            fcmSender.sendMessageTopic(topic, "테스트", "주제 구독 테스트");
            return new ResponseEntity("success", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity("fail", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/topic2")
    public ResponseEntity topicTest2(@RequestParam String topic){
        try {
            fcmSender.sendMessageTopicV2(topic, "테스트", "주제 구독 테스트");
            return new ResponseEntity("success", HttpStatus.OK);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
            return new ResponseEntity("fail", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/register")
    public ResponseEntity register(@RequestParam String name,
                                   @RequestParam String token){
        fcmSender.register(token, name);
        return new ResponseEntity("success", HttpStatus.OK);
    }

    @GetMapping("/java/multimessage")
    public ResponseEntity sendMulti(){
        try {
            fcmSender.sendMultiToken("테스트", "멀티 메시지 테스트");
            return new ResponseEntity("success", HttpStatus.OK);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
            return new ResponseEntity("fail", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/tokenmessage")
    public FcmRequest viewToken(){
        FcmMessage message = new TokenMessage("test", "test2", "test3");
        return new FcmRequest(message);
    }

    @GetMapping("/topicmessage")
    public FcmRequest viewTopic(){
        FcmMessage message = new TopicMessage("test", "test2", "test3");
        return new FcmRequest(message);
    }

    @GetMapping("/conditionmessage")
    public FcmRequest viewCondition(){
        FcmMessage message = new ConditionMessage("test", "test2", "test3");
        return new FcmRequest(message);
    }

}
