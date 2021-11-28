package in.koala.serviceImpl;

import in.koala.domain.fcm.ConditionMessage;
import in.koala.domain.fcm.TokenMessage;
import in.koala.domain.fcm.TopicMessage;
import in.koala.service.FcmTestService;
import in.koala.util.FcmSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class FcmTestServiceImpl implements FcmTestService {
    @Resource
    FcmSender fcmSender;

    /*
    FcmSender 사용 예시
    sendMessage 사용은 동일하고 메시지 종류 다르게 만들어서 인자로 넣으면
    메시지 클래스에 따라서 전송
     */
    public String sendTokenTest(String token) throws Exception{
        fcmSender.sendMessage(
                new TokenMessage("test", "TokenTest", token)
        );
        return "success";
    }

    public String sendTopicTest(String topic) throws Exception{
        fcmSender.sendMessage(
                new TopicMessage("test", "TopicTest", topic)
        );
        return "success";
    }

    public String sendConditionTest(String condition) throws Exception{
        fcmSender.sendMessage(
                new ConditionMessage("test", "ConditionTest", condition)
        );
        return "success";
    }
}
