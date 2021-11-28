package in.koala.controller;

import in.koala.annotation.Auth;
import in.koala.annotation.Xss;
import in.koala.service.CrawlingService;
import in.koala.service.KeywordService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KeywordController {

    private final KeywordService keywordService;

    @Xss
    @Auth
    @ApiOperation(value ="키워드 삽입" , notes = "사용자가 지정한 키워드를 등록한다." , authorizations = @Authorization(value = "Bearer +accessToken"))
    @GetMapping(value = "/keyword")
    public void registerKeyword(@RequestParam(name = "keyword") String keyword,
                                @RequestParam(name = "site") short site,
                                @RequestParam(name = "isImportant") boolean isImportant){
        keywordService.registerKeyword(keyword, site, isImportant);
    }
}
