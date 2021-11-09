package in.koala.serviceImpl;

import in.koala.service.FcmTestService;
import in.koala.util.FcmSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class FcmTestServiceImpl implements FcmTestService {
    @Resource
    FcmSender fcmSender;

    public String sendTest(String token) throws Exception {
        fcmSender.sendMessage(token, "test", "테스트중입니다");
        return "success";
    }
}
