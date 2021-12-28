package in.koala.controller;

import in.koala.annotation.Auth;
import in.koala.annotation.Xss;
import in.koala.domain.Keyword;
import in.koala.domain.Notice;
import in.koala.domain.response.CustomBody;
import in.koala.service.CrawlingService;
import in.koala.service.KeywordService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class KeywordController {

    private final KeywordService keywordService;
    
    @Xss
    @Auth
    @ApiOperation(value ="키워드 조회" , notes = "사용자가 지정한 키워드를 조회한다." , authorizations = @Authorization(value = "Bearer +accessToken"))
    @GetMapping(value = "/keyword")
    public ResponseEntity<List<Keyword>> myKeywordList(){
        return new ResponseEntity(CustomBody.of(keywordService.myKeywordList(), HttpStatus.OK), HttpStatus.OK);
    }

    @Xss
    @Auth
    @ApiOperation(value ="키워드 추가" , notes = "사용자가 지정한 키워드를 등록한다." , authorizations = @Authorization(value = "Bearer +accessToken"))
    @PostMapping(value = "/keyword")
    public void registerKeyword(@RequestBody Keyword keyword){
        keywordService.registerKeyword(keyword);
    }

    @Xss
    @Auth
    @ApiOperation(value = "키워드 삭제", notes = "사용자가 지정한 키워드를 삭제한다.", authorizations = @Authorization(value = "Bearer +accessToken"))
    @PatchMapping(value = "/keyword")
    public void deleteKeyword(@RequestParam(name = "keyword-name") String keywordName){
        keywordService.deleteKeyword(keywordName);
    }

    @Xss
    @Auth
    @ApiOperation(value = "키워드 수정", notes = "사용자가 지정한 키워드를 수정한다.", authorizations = @Authorization(value = "Bearer +accessToken"))
    @PutMapping(value = "/keyword")
    public void modifyKeyword(@RequestParam(name = "keyword-name") String keywordName,
                              @RequestBody Keyword keyword){
        keywordService.modifyKeyword(keywordName, keyword);
    }

    @Xss
    @Auth
    @ApiOperation(value = "키워드 목록 페이지 - 전체", notes = "키워드 목록에서 하나의 키워드를 선택한 후 받은 알람을 본다.", authorizations = @Authorization(value = "Bearer +accessToken"))
    @GetMapping(value = "/keyword/list")
    public ResponseEntity<List<Notice>> getKeywordNotice(@RequestParam(name = "keyword-name") String keywordName,
                                                         @RequestParam(name = "site", required = false) String site){
        List<Notice> result = keywordService.getKeywordNotice(keywordName, site);
        if(result.isEmpty())
            return new ResponseEntity(CustomBody.of("받은 알림이 없습니다.", HttpStatus.BAD_REQUEST), HttpStatus.OK);
        else
            return new ResponseEntity (CustomBody.of(result,HttpStatus.OK), HttpStatus.OK);
    }

}
