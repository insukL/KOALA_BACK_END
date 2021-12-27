package in.koala.serviceImpl;

import in.koala.domain.fcm.TokenMessage;
import in.koala.enums.ErrorMessage;
import in.koala.exception.KeywordPushException;
import in.koala.mapper.KeywordPushMapper;
import in.koala.service.CrawlingService;
import in.koala.service.KeywordPushService;
import in.koala.service.KeywordService;
import in.koala.service.UserService;
import in.koala.util.FcmSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeywordPushServiceImpl implements KeywordPushService {

    private final UserService userService;
    private final FcmSender fcmSender;
    private final KeywordPushMapper keywordPushMapper;
    private final CrawlingService crawlingService;

    @Override
    public void pushKeywordAtOnce(String deviceToken) throws Exception {

        Long userId = userService.getLoginUserInfo().getId();

        //crawlingService.executeAll();

        Timestamp latelyCrawlingTime = crawlingService.getLatelyCrawlingTime();

        List<Map<String, String>> tmp = keywordPushMapper.pushKeywordByLatelyCrawlingTime(latelyCrawlingTime, userId);

        try{
            for(Map<String, String> map : tmp){
                String title = map.get("title");
                String url = map.get("url");
                System.out.println("제목 : " + title);
                System.out.println("url : " + url);
                fcmSender.sendMessage(new TokenMessage(title, url, deviceToken));
            }
        }
        catch (Exception e){
            throw new KeywordPushException(ErrorMessage.FAILED_TO_SEND_NOTIFICATION);
        }

    }

    @Override
    public void pushKeyword(String deviceToken) {

        Long userId = userService.getLoginUserInfo().getId();
        List<Map<String, String>> tmp = keywordPushMapper.pushKeyword(userId);

        try{
            for(Map<String, String> map : tmp){
                String title = map.get("title");
                String url = map.get("url");
                System.out.println("제목 : " + title);
                System.out.println("url : " + url);
                fcmSender.sendMessage(new TokenMessage(title, url, deviceToken));
            }
        }
        catch (Exception e){
            throw new KeywordPushException(ErrorMessage.FAILED_TO_SEND_NOTIFICATION);
        }
    }

}
