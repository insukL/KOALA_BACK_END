package in.koala.serviceImpl;

import in.koala.domain.Crawling;
import in.koala.domain.DeviceToken;
import in.koala.domain.Keyword;
import in.koala.domain.fcm.TokenMessage;
import in.koala.domain.fcm.TopicMessage;
import in.koala.enums.CrawlingSite;
import in.koala.enums.ErrorMessage;
import in.koala.exception.KeywordPushException;
import in.koala.mapper.DeviceTokenTestMapper;
import in.koala.mapper.KeywordPushMapper;
import in.koala.mapper.NoticeMapper;
import in.koala.mapper.UserMapper;
import in.koala.service.CrawlingService;
import in.koala.service.KeywordPushService;
import in.koala.service.KeywordService;
import in.koala.service.UserService;
import in.koala.util.EnConverter;
import in.koala.util.FcmSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.sql.Timestamp;

@Service
@Transactional
@RequiredArgsConstructor
public class KeywordPushServiceImpl implements KeywordPushService {

    private final UserService userService;
    private final FcmSender fcmSender;
    private final KeywordPushMapper keywordPushMapper;
    private final CrawlingService crawlingService;
    private final NoticeMapper noticeMapper;
    private final EnConverter enConverter;
    private final DeviceTokenTestMapper tokenMapper;


    //TODO : 아래 메소드 2개 정리
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
                fcmSender.sendMessage(new TokenMessage("키워드의 글이 등록되었습니다", title, url, deviceToken));
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
                noticeMapper.insertNotice(map);
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

    //Crawling Json 파싱 실패에 따른 테스트용 메소드
    @Override
    public void pushKeyword(List<String> keyword, String title, String url, Short site) throws Exception{
        TopicMessage message = new TopicMessage("키워드의 글이 등록되었습니다.",
                                                    title,
                                                    url,
                                                    null );
        for(String word : keyword){
            message.setTopic(enConverter.ktoe(word) + site.toString());
            System.out.println(message.getTopic());
            fcmSender.sendMessage(message);
        }
    }

    @Override
    public void subscribe(Keyword keyword, Long id) throws Exception{
        //UserMapper 임시 사용
        List<String> tokenList = tokenMapper.getDeviceTokenById(id);
        for(CrawlingSite site : keyword.getSiteList()){
            fcmSender.subscribe(tokenList,
                    enConverter.ktoe(keyword.getName()) + site.getCode().toString());
            System.out.println(enConverter.ktoe(keyword.getName()) + site.getCode().toString());
        }
    }

    @Override
    public void unsubscribe(Keyword keyword, Long id) throws Exception{
        //UserMapper 임시 사용
        List<String> tokenList = tokenMapper.getDeviceTokenById(id);
        for(CrawlingSite site : keyword.getSiteList()){
            fcmSender.unsubscribe(tokenList,
                    enConverter.ktoe(keyword.getName()) + site.getCode().toString());
            System.out.println(enConverter.ktoe(keyword.getName()) + site.getCode().toString());
        }
    }

    @Override
    public void modifySubscription(Keyword oldWord, Keyword newWord, Long id) throws Exception{
        System.out.println(oldWord.getSiteList());
        System.out.println(newWord.getSiteList());
        Keyword keyword = new Keyword();
        keyword.setName(oldWord.getName());

        //기존 키워드 구독 해제
        List<CrawlingSite> temp = new ArrayList<>();
        System.out.println(oldWord.getSiteList());
        System.out.println(newWord.getSiteList());
        for(CrawlingSite site : oldWord.getSiteList()){
            if(!newWord.getSiteList().contains(site)) temp.add(site);
        }
        keyword.setSiteList(temp);
        this.unsubscribe(keyword, id);
        System.out.println("해제 키워드 목록");
        System.out.println(temp);

        //신규 키워드 구독
        temp.clear();
        for(CrawlingSite site : newWord.getSiteList()){
            if(!oldWord.getSiteList().contains(site)) temp.add(site);
        }
        keyword.setSiteList(temp);
        this.subscribe(keyword, id);
        System.out.println("추가 키워드 목록");
        System.out.println(temp);
    }
}
