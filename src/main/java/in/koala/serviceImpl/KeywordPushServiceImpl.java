package in.koala.serviceImpl;

import in.koala.domain.Crawling;
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
            List<Map<String, String>> tmp = keywordPushMapper.pushKeyword(userId);
            for(Map<String, String> map : tmp){
                System.out.println(map.get("title") + " " + map.get("url"));
                fcmSender.sendMessage(deviceToken, map.get("title"), map.get("url"));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
