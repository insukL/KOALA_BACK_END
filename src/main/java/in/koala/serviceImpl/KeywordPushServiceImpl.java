package in.koala.serviceImpl;

import in.koala.domain.Crawling;
import in.koala.domain.Keyword;
import in.koala.domain.fcm.TokenMessage;
import in.koala.domain.fcm.TopicMessage;
import in.koala.enums.CrawlingSite;
import in.koala.enums.ErrorMessage;
import in.koala.exception.KeywordPushException;
import in.koala.mapper.KeywordPushMapper;
import in.koala.service.KeywordPushService;
import in.koala.service.UserService;
import in.koala.util.EnConverter;
import in.koala.util.FcmSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeywordPushServiceImpl implements KeywordPushService {

    private final UserService userService;
    private final FcmSender fcmSender;
    private final KeywordPushMapper keywordPushMapper;
    private final EnConverter enConverter;

    @Override
    public void pushKeyword(String deviceToken) {

        Long userId = userService.getLoginUserInfo().getId();
        List<Map<String, String>> tmp = keywordPushMapper.pushKeyword(userId);

        try{
            for(Map<String, String> map : tmp){
                String title = map.get("title");
                String url = map.get("url");
                fcmSender.sendMessage(new TokenMessage("키워드의 글이 등록되었습니다", title, url, deviceToken));
            }
        }
        catch (Exception e){
            throw new KeywordPushException(ErrorMessage.FAILED_TO_SEND_NOTIFICATION);
        }
    }

    //Topic Message 활용안
    @Override
    public void pushKeyword(List<String> keyword, Crawling crawling) throws Exception{
        TopicMessage message = new TopicMessage("키워드의 글이 등록되었습니다.",
                                                    crawling.getTitle(),
                                                    crawling.getUrl(),
                                                    null );
        for(String word : keyword){
            message.setTopic(enConverter.ktoe(word) + crawling.getSite().toString());
            fcmSender.sendMessage(message);
        }
    }

    @Override
    public void subscribe(Keyword keyword, String deviceToken) throws Exception{
        for(CrawlingSite site : keyword.getSiteList()){
            fcmSender.subscribe(Arrays.asList(deviceToken),
                    enConverter.ktoe(keyword.getName()) + site.getCode().toString());
        }
    }

    @Override
    public void unsubscribe(Keyword keyword, String deviceToken) throws Exception{
        for(CrawlingSite site : keyword.getSiteList()){
            fcmSender.unsubscribe(Arrays.asList(deviceToken),
                    enConverter.ktoe(keyword.getName()) + site.getCode().toString());
        }
    }
}
