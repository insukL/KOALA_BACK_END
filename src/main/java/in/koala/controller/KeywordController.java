package in.koala.controller;

import in.koala.annotation.Auth;
import in.koala.annotation.Xss;
import in.koala.domain.Keyword;
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
    
//    @Xss
//    @Auth
//    @ApiOperation(value ="키워드 조회" , notes = "사용자가 지정한 키워드를 조회한다." , authorizations = @Authorization(value = "Bearer +accessToken"))
//    @GetMapping(value = "/keyword")
//    public ResponseEntity<List<Keyword>> myKeywordList(){
//        return new ResponseEntity(keywordService.myKeywordList(), HttpStatus.OK);
//    }


    @Xss
    @Auth
    @ApiOperation(value ="키워드 추가" , notes = "사용자가 지정한 키워드를 등록한다." , authorizations = @Authorization(value = "Bearer +accessToken"))
    @PostMapping(value = "/keyword")
    public void registerKeyword(@RequestBody Keyword keyword){
        keywordService.registerKeyword(keyword);
    }

//    @Xss
//    @Auth
//    @ApiOperation(value = "키워드 삭제", notes = "사용자가 지정한 키워드를 삭제한다.", authorizations = @Authorization(value = "Bearer +accessToken"))
//    @PatchMapping(value = "/keyword")
//    public void deleteKeyword(@RequestParam(name = "keyword-id") String keywordId){
//        keywordService.deleteKeyword(keywordId);
//    }

//    @Xss
//    @Auth
//    @ApiOperation(value = "키워드 수정", notes = "사용자가 지정한 키워드를 수정한다.", authorizations = @Authorization(value = "Bearer +accessToken"))
//    @PutMapping(value = "/keyword")
//    public void modifyKeyword(@RequestParam(name = "keyword-id") String keywordId,
//                              @RequestParam(name = "keyword-name") String keywordName){
//        keywordService.modifyKeyword(keywordId, keywordName);
//    }
}
