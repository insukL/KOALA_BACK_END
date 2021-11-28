package in.koala.controller;

import in.koala.annotation.Auth;
import in.koala.annotation.Xss;
import in.koala.domain.Crawling;
import in.koala.service.KeywordPushService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class KeywordPushController {

    private final KeywordPushService keywordPushService;

    @Xss
    @Auth
    @ApiOperation(value ="키워드 푸쉬 메세지" , notes = "사용자가 지정한 키워드를 크롤링한 데이터에서 찾아 알림한다." , authorizations = @Authorization(value = "Bearer +accessToken"))
    @GetMapping(value = "/fcm/keyword")
    public ResponseEntity<List<Crawling>> pushKeyword(@RequestParam(name = "device-token") String deviceToken){
        return new ResponseEntity<List<Crawling>>(keywordPushService.pushKeyword(deviceToken), HttpStatus.OK);
    }
}
