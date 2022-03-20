package in.koala.util;

import in.koala.domain.PushNotice;
import in.koala.mapper.KeywordPushMapper;
import in.koala.service.CrawlingService;
import in.koala.service.KeywordPushService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class Scheduler {
    /**
    1초에 한번씩 호출하는 fixedDelay
     */

    private final KeywordPushService keywordPushService;
    private final CrawlingService crawlingService;
    private final KeywordPushMapper keywordPushMapper;

    @Scheduled(fixedDelay = 600000)
    public void scheduleFixedRateTask() throws Exception {

        crawlingService.executeAll();
        Timestamp mostRecentCrawlingTime = crawlingService.getMostRecentCrawlingTime();

        log.info(mostRecentCrawlingTime.toString());
        List<PushNotice> pushNoticeList = keywordPushMapper.pushKeywordByLatelyCrawlingTime(mostRecentCrawlingTime);

        for(PushNotice notice : pushNoticeList){
            System.out.println(notice.getTokenList());
            System.out.println(notice.getKeyword());
            System.out.println(notice.getSite());
            System.out.println(notice.getUrl());
            keywordPushService.pushNotification(notice.getTokenList(), notice.getKeyword(),
                    notice.getSite(), notice.getUrl());
        }

    }
    /**
     1초에 한번씩 호출하는 cron expression

     그 전에crontab 주기설정 방법부터 알아보자.

     *           *　　　　　　*　　　　　　*　　　　　　*　　　　　　*
     초(0-59)   분(0-59)　　시간(0-23)　　일(1-31)　　월(1-12)　　요일(0-7)
     각 별 위치에 따라 주기를 다르게 설정 할 수 있다.
     순서대로 초-분-시간-일-월-요일 순이다. 그리고 괄호 안의 숫자 범위 내로 별 대신 입력 할 수도 있다.
     요일에서 0과 7은 일요일이며, 1부터 월요일이고 6이 토요일이다.
     */
//    @Scheduled(cron = "* * * * * ?")
    public void scheduleCronExpressionTask() {
        System.out.println("Current Thread : "+ Thread.currentThread().getName());
    }
}
