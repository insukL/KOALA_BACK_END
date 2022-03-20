package in.koala.controller;

import in.koala.domain.Crawling;
import in.koala.enums.CrawlingSite;
import in.koala.serviceImpl.KeywordPushServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/fcm-test")
public class FcmTestController {
    @Resource
    private KeywordPushServiceImpl keywordPushService;


    @PostMapping("/keyword")
    @ApiOperation(value ="키워드 푸시 테스트", notes = "키워드 알람 발송 실사용X")
    public ResponseEntity pushKeyword(@RequestParam CrawlingSite site,
                                      @RequestParam String url,
                                      @RequestParam List<String> tokens,
                                      @RequestParam String keyword)throws Exception{
        keywordPushService.pushNotification(tokens, keyword, site, url);
        return new ResponseEntity<String>("success", HttpStatus.OK);
    }
}
