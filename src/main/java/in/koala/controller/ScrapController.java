package in.koala.controller;

import in.koala.annotation.Auth;
import in.koala.domain.Scrap;
import in.koala.domain.response.CustomBody;
import in.koala.service.ScrapService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.annotation.Resource;
import java.util.List;

@RestController
public class ScrapController {

    @Resource
    ScrapService scrapService;

    @Auth
    @ApiOperation(value = "보관함 이동", notes = "보관함으로 이동합니다.\n크롤링한 글의 id가 필요합니다.", authorizations = @Authorization(value = "Bearer +accessToken"))
    @PostMapping(value = "/scrap")
    public ResponseEntity Scrap(@RequestBody Scrap scrap) throws Exception {
        scrapService.Scrap(scrap);
        return new ResponseEntity(CustomBody.of("보관함으로 이동되었습니다.", HttpStatus.OK), HttpStatus.OK);
    }

    @Auth
    @ApiOperation(value = "보관함 조회", notes = "보관함 조회입니다.", authorizations = @Authorization(value = "Bearer +accessToken"))
    @GetMapping(value = "/scrap")
    public ResponseEntity getScrap() throws Exception {
        return new ResponseEntity(CustomBody.of(scrapService.getScrap(), HttpStatus.OK), HttpStatus.OK);
    }

    @Auth
    @ApiOperation(value = "보관함 선택 삭제", notes = "보관함 삭제입니다.\nList 형태로 입력 받습니다.\nex) [1, 11, 111]\n메모도 함께 삭제됩니다.", authorizations = @Authorization(value = "Bearer +accessToken"))
    @DeleteMapping(value = "/scrap")
    public ResponseEntity deleteScrap(@RequestBody List<Long> crawlingId) throws Exception {
        scrapService.deleteScrap(crawlingId);
        return new ResponseEntity(CustomBody.of("보관함에서 삭제되었습니다.", HttpStatus.OK), HttpStatus.OK);
    }

}
