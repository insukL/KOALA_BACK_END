package in.koala.serviceImpl;

import in.koala.domain.Crawling;
import in.koala.domain.DeviceToken;
import in.koala.domain.Keyword;
import in.koala.domain.fcm.ConditionMessage;
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
    public void pushKeywordAtOnce() throws Exception {

        crawlingService.executeAll();

        Timestamp latelyCrawlingTime = crawlingService.getLatelyCrawlingTime();

        List<Map<String, String>> tmp = keywordPushMapper.pushKeywordByLatelyCrawlingTime(latelyCrawlingTime);

        for(Map value : tmp){
            StringBuilder sb = new StringBuilder();
            ConditionMessage message = new ConditionMessage("키워드의 글이 등록되었습니다.",
                    value.get("title").toString(),
                    value.get("url").toString(),
                    null);

            sb.append("\'" + enConverter.ktoe(value.get("name").toString()) + "1" + "\' in topics");
            message.setCondition(sb.toString());
            fcmSender.sendMessage(message);
            noticeMapper.insertNotice(value);
        }
    }

    // ---------------------------------------- 위 현승 / 아래 인석 -------------------------------------------------
    /*
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
     */

    @Override
    public void pushKeyword(List<String> keyword, Crawling crawling) throws Exception{
        StringBuilder sb = new StringBuilder();
        ConditionMessage message = new ConditionMessage("키워드의 글이 등록되었습니다.",
                                                    crawling.getTitle(),
                                                    crawling.getUrl(),
                                                    null );
        short cnt = 0;
        for(String word : keyword){
            if(cnt >= 5){
                message.setCondition(sb.toString());
                fcmSender.sendMessage(message);
                sb.setLength(0);
                cnt = 0;
            }
            if(cnt > 0) sb.append(" || ");
            sb.append("\'" + enConverter.ktoe(word) + crawling.getSite().toString() + "\' in topics");
            ++cnt;
        }
        message.setCondition(sb.toString());
        fcmSender.sendMessage(message);
    }

    //Crawling Json 파싱 실패에 따른 테스트용 메소드
    @Override
    public void pushKeyword(List<String> keyword, String title, String url, Short site) throws Exception{
        StringBuilder sb = new StringBuilder();
        ConditionMessage message = new ConditionMessage("키워드의 글이 등록되었습니다.",
                                                            title,
                                                            url,
                                                            null );
        short cnt = 0;
        for(String word : keyword){
            if(cnt >= 5){
                message.setCondition(sb.toString());
                System.out.println("다섯개 이상일때 : " + sb);
                fcmSender.sendMessage(message);
                sb.setLength(0);
                System.out.println("cnt>= 5 : " + sb);
                cnt = 0;
            }
            if(cnt > 0) sb.append(" || ");
            sb.append("\'" + enConverter.ktoe(word) + site + "\' in topics");
            ++cnt;
            System.out.println("cnt>0 : " + sb);
        }
        message.setCondition(sb.toString());
        System.out.println("최종 : "+ sb);
        fcmSender.sendMessage(message);
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
    public void modifySubscription(List<CrawlingSite> oldSite,
                                   List<CrawlingSite> newSite,
                                   Long id, String keywordName) throws Exception{
        System.out.println("구독 해제 목록");
        System.out.println(oldSite);
        System.out.println("새 구독 목록");
        System.out.println(newSite);

        Keyword keyword = Keyword.builder()
                .name(keywordName)
                .siteList(oldSite)
                .build();

        //기존 키워드 구독 해제
        this.unsubscribe(keyword, id);

        //신규 키워드 구독
        keyword.setSiteList(newSite);
        this.subscribe(keyword, id);
    }
}
