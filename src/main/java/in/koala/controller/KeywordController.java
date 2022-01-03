package in.koala.controller;

import in.koala.annotation.Auth;
import in.koala.annotation.Xss;
import in.koala.domain.Keyword;
import in.koala.domain.Notice;
import in.koala.domain.response.CustomBody;
import in.koala.service.CrawlingService;
import in.koala.service.KeywordService;
import io.swagger.annotations.Api;
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
    public void registerKeyword(@RequestBody Keyword keyword) throws Exception {
        keywordService.registerKeyword(keyword);
    }

    @Xss
    @Auth
    @ApiOperation(value = "키워드 삭제", notes = "사용자가 지정한 키워드를 삭제한다.", authorizations = @Authorization(value = "Bearer +accessToken"))
    @PatchMapping(value = "/keyword")
    public void deleteKeyword(@RequestParam(name = "keyword-name") String keywordName) throws Exception {
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
    @ApiOperation(value = "키워드 입력시 검색", notes = "키워드 등록 및 수정 시 추천 키워드를 제공한다.", authorizations = @Authorization(value = "Bearer +accessToken"))
    @GetMapping(value = "/keyword/search")
    public ResponseEntity<List<String>> searchKeyword(@RequestParam(name = "keyword") String keyword){

        List<String> result = keywordService.searchKeyword(keyword);

        if(result.isEmpty())
            return new ResponseEntity(CustomBody.of("검색 결과가 없습니다.", HttpStatus.OK), HttpStatus.OK);
        else
            return new ResponseEntity(CustomBody.of(result, HttpStatus.OK), HttpStatus.OK);
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

    @Xss
    @Auth
    @ApiOperation(value = "키워드 목록 페이지 - 검색", notes = "키워드 목록에서 하나의 키워드를 선택한 후 검색한 후에 검색 결과에 대한 알림 반환", authorizations = @Authorization(value = "Bearer +accessToken"))
    @GetMapping(value = "/keyword/list/search")
    public ResponseEntity<List<Notice>> getSearchNotice(@RequestParam(name = "keyword-name") String keywordName,
                                                        @RequestParam(name = "site", required = false) String site,
                                                        @RequestParam(name = "word") String word){
        List<Notice> result = keywordService.getSearchNotice(keywordName, site, word);
        if(result.isEmpty())
            return new ResponseEntity(CustomBody.of("받은 알림이 없습니다.", HttpStatus.BAD_REQUEST), HttpStatus.OK);
        else
            return new ResponseEntity (CustomBody.of(result,HttpStatus.OK), HttpStatus.OK);
    }

    @Xss
    @Auth
    @ApiOperation(value = "키워드 목록 페이지 - 알림 삭제", notes = "키워드 목록에서 하나의 키워드를 선택한 후 나온 알림에 대해서 \n 클릭시 알림 삭제", authorizations = @Authorization(value = "Bearer +accessToken"))
    @PatchMapping(value = "/keyword/list/notice")
    public void deleteNotice(@RequestParam(name = "notice-id") String noticeId){
        keywordService.deletedNotice(noticeId);
    }

    @Xss
    @Auth
    @ApiOperation(value = "키워드 목록 페이지 - 알림 읽음 처리", notes = "키워드 목록에서 하나의 키워드를 선택한 후 나온 알림에 대해서 \n 클릭시 알림 읽음 처리", authorizations = @Authorization(value = "Bearer +accessToken"))
    @PatchMapping(value = "/keyword/list/notice/reading-check")
    public ResponseEntity noticeRead(@RequestParam(name = "notice-id") String noticeId){
        if(keywordService.noticeRead(noticeId)){
            return new ResponseEntity(CustomBody.of("알림을 읽었습니다.", HttpStatus.OK), HttpStatus.OK);
        }
        else{
            return new ResponseEntity(CustomBody.of("알림읽는것을 실패하였습니다.", HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

}
