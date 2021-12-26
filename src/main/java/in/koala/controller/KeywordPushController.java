package in.koala.controller;

import in.koala.annotation.Auth;
import in.koala.annotation.Xss;
import in.koala.service.KeywordPushService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KeywordPushController {

    private final KeywordPushService keywordPushService;

    @Xss
    @Auth
    @ApiOperation(value ="디바이스 토큰을 이용한 키워드 푸쉬 메세지" , notes = "사용자가 지정한 키워드를 크롤링한 데이터에서 찾아 알림한다." , authorizations = @Authorization(value = "Bearer +accessToken"))
    @GetMapping(value = "/fcm/keyword")
    public void pushKeyword(@RequestParam(name = "device-token") String deviceToken){
        keywordPushService.pushKeyword(deviceToken);
    }

    @Xss
    @Auth
    @ApiOperation(value = "크롤링한 시점이후 바로 데이터 처리해주기 TEST", notes = "크롤링이 완료된 순간 유저가 지정한 키워드에 대한 크롤링 데이터 알림", authorizations = @Authorization(value = "Bearer +accessToken"))
    @GetMapping("/fcm/at-once")
    public void pushKeywordAtOnce(@RequestParam(name = "device-token") String deviceToken) throws Exception{
        keywordPushService.pushKeywordAtOnce(deviceToken);
    }
}
