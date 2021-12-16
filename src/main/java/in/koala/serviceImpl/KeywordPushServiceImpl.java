package in.koala.serviceImpl;

import in.koala.domain.Crawling;
import in.koala.domain.fcm.TokenMessage;
import in.koala.mapper.KeywordPushMapper;
import in.koala.service.KeywordPushService;
import in.koala.service.UserService;
import in.koala.util.FcmSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeywordPushServiceImpl implements KeywordPushService {

    private final UserService userService;
    private final FcmSender fcmSender;
    private final KeywordPushMapper keywordPushMapper;

    @Override
    public List<Crawling> pushKeyword(String deviceToken) {

        try{
            Long userId = userService.getLoginUserInfo().getId();
            System.out.println("user Id : " + userId);
            List<Map<String, String>> tmp = keywordPushMapper.pushKeyword(userId);
            System.out.println(tmp);
            for(Map<String, String> map : tmp){
                String title = map.get("title");
                String url = map.get("url");
                System.out.println(title + " " + url);
                fcmSender.sendMessage(new TokenMessage(title, url, deviceToken));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
