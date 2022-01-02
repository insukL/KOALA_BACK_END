package in.koala.controller;

import in.koala.annotation.Auth;
import in.koala.domain.Crawling;
import in.koala.domain.DeviceToken;
import in.koala.domain.Keyword;
import in.koala.service.UserService;
import in.koala.serviceImpl.DeviceTokenTestServiceImpl;
import in.koala.serviceImpl.KeywordPushServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.Timestamp;
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
    public ResponseEntity pushKeyword(@RequestBody List<String> keywordList,
                                      @RequestParam String title,
                                      @RequestParam String url,
                                      @RequestParam Short site) throws Exception{
        keywordPushService.pushKeyword(keywordList, title, url, site);
        return new ResponseEntity<String>("success", HttpStatus.OK);
    }
}
