package in.koala.controller;

import in.koala.annotation.Auth;
import in.koala.domain.Crawling;
import in.koala.domain.DeviceToken;
import in.koala.domain.Keyword;
import in.koala.controller.response.BaseResponse;
import in.koala.service.UserService;
import in.koala.serviceImpl.DeviceTokenTestServiceImpl;
import in.koala.serviceImpl.KeywordPushServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
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

    @Resource
    private DeviceTokenTestServiceImpl tokenService;

    @Resource
    private UserService userService;

    @Auth
    @PostMapping("/device")
    @ApiOperation(value ="디바이스 토큰 등록 테스트" , notes = "디바이스 토큰 등록 실사용X" , authorizations = @Authorization(value = "Bearer +accessToken"))
    public ResponseEntity insertDeviceToken(@RequestBody DeviceToken deviceToken){
        tokenService.insertDeviceToken(deviceToken, userService.getLoginUserInfo().getId());
        return new ResponseEntity<String>("success", HttpStatus.OK);
    }

    @Auth
    @PostMapping("/sub")
    @ApiOperation(value ="키워드 구독 테스트" , notes = "키워드 구독 실사용X" , authorizations = @Authorization(value = "Bearer +accessToken"))
    public ResponseEntity subscribeKeyword(@RequestBody Keyword keyword) throws Exception {
        keywordPushService.subscribe(keyword, userService.getLoginUserInfo().getId());
        return new ResponseEntity<String>("success", HttpStatus.OK);
    }

    @Auth
    @PostMapping("/unsub")
    @ApiOperation(value ="키워드 구독 해제 테스트", notes = "키워드 구독 해제 실사용X", authorizations = @Authorization(value = "Bearer +accessToken"))
    public ResponseEntity unsubscribeKeyword(@RequestBody Keyword keyword) throws Exception{
        keywordPushService.unsubscribe(keyword, userService.getLoginUserInfo().getId());
        return new ResponseEntity<String>("success", HttpStatus.OK);
    }

    @PostMapping("/keyword")
    @ApiOperation(value ="키워드 푸시 테스트", notes = "키워드 알람 발송 실사용X")
    public ResponseEntity pushKeyword(@RequestBody Crawling crawling,
                                      @RequestParam List<String> tokens,
                                      @RequestParam String keyword)throws Exception{
        keywordPushService.pushNotification(tokens, keyword, crawling);
        return new ResponseEntity<String>("success", HttpStatus.OK);
    }

    @Auth
    @PostMapping("/new-sub")
    @ApiOperation(value = "키워드 구독 수정 테스트", notes = "키워드 구독 수정 실사용X", authorizations = @Authorization(value = "Bearer +accessToken"))
    public ResponseEntity modifySubscription(@RequestBody Keyword keyword) throws Exception{
//        Keyword old = new Keyword();
//        old.setName(keyword.getName());
//        List<CrawlingSite> list = new ArrayList<>();
//        Set<Integer> temp = keywordMapper.getKeywordSite(userService.getLoginUserInfo().getId(), old.getName());
//        for(Integer t : temp){
//            for(CrawlingSite c : CrawlingSite.values()){
//                if(c.getCode() == t) list.add(c);
//            }
//        }
//        old.setSiteList(list);
//        keywordPushService.modifySubscription(old, keyword, userService.getLoginUserInfo().getId());
        return new ResponseEntity<String>("success", HttpStatus.OK);
    }

    // --------------------------------- 위 인석 / 아래 현승 -------------------------------------------------------
    // To. 인석
    // 크롤링한 시점에서 키워드 푸쉬 될 수 있도록 변경해서 쓸게....
    // KeywordPushController를 삭제하고 여기서 한번에 테스트 하려구

    @Auth
    @PostMapping("/keyword2")
    @ApiOperation(value ="키워드 푸시 테스트2", notes = "키워드 알람 발송 실사용X", authorizations = @Authorization(value = "Bearer +accessToken"))
    public ResponseEntity pushKeywordAtOnce() throws Exception{
        keywordPushService.pushKeywordAtOnce();
        return new ResponseEntity(BaseResponse.of("success", HttpStatus.OK), HttpStatus.OK);
    }
}
