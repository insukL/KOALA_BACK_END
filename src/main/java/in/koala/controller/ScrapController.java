package in.koala.controller;

import in.koala.domain.Scrap;
import in.koala.service.ScrapService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.annotation.Resource;

@RestController
public class ScrapController {

    @Resource
    ScrapService scrapService;

    /*
    @ApiOperation(value = "보관함 조회", notes = "보관함 조회 api 입니다.")
    @GetMapping(value = "/scrap")
    public ResponseEntity getScrap() throws Exception {
        return new ResponseEntity(scrapService.getScrap(board_id), HttpStatus.OK);
    }
     */

    @ApiOperation(value = "보관함 이동", notes = "보관함 이동 api 입니다.")
    @PostMapping(value = "/scrap")
    public ResponseEntity Scrap(@RequestBody Scrap scrap) throws Exception {
        scrapService.Scrap(scrap);
        return new ResponseEntity("보관함으로 이동되었습니다.", HttpStatus.OK);
    }

    @ApiOperation(value = "보관함 선택 삭제", notes = "보관함 선택 삭제 api 입니다.")
    @DeleteMapping(value = "/scrap/{board_id}")
    public ResponseEntity deleteScrap(@RequestParam Long board_id) throws Exception {
        scrapService.deleteScrap(board_id);
        return new ResponseEntity("선택 삭제되었습니다.", HttpStatus.OK);
    }

    @ApiOperation(value = "보관함 전체 삭제", notes = "보관함 전체 삭제 api 입니다.")
    @DeleteMapping(value = "/scrap")
    public ResponseEntity deleteAllScrap(Long user_id) throws Exception {
        scrapService.deleteAllScrap(user_id);
        return new ResponseEntity("전체 삭제되었습니다.", HttpStatus.OK);
    }


}
