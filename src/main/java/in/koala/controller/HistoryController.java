package in.koala.controller;

import in.koala.annotation.Auth;
import in.koala.annotation.Xss;
import in.koala.domain.Notice;
import in.koala.domain.response.CustomBody;
import in.koala.service.HistoryService;
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
public class HistoryController {

    private final HistoryService historyService;

    @Xss
    @Auth
    @ApiOperation(value = "히스토리 조회",
            notes = "사용자가 받은 알림에 대한 전체 내역을 보여준다.",
            authorizations = @Authorization(value = "Bearer +accessToken"))
    @GetMapping(value = "/history")
    public ResponseEntity<List<Notice>> getEveryNotice(@RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum){

        List<Notice> result = historyService.getEveryNotice(pageNum);

        if(result.isEmpty()){
            return new ResponseEntity(CustomBody.of("알림이 없습니다.", HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
        else{
            return new ResponseEntity(CustomBody.of(result, HttpStatus.OK), HttpStatus.OK);
        }
    }
}
