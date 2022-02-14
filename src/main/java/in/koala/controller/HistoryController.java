package in.koala.controller;

import in.koala.annotation.Auth;
import in.koala.annotation.Xss;
import in.koala.domain.Notice;
import in.koala.controller.response.BaseResponse;
import in.koala.service.HistoryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<Notice>> getEveryNotice(
            @RequestParam(value = "page-num", required = false, defaultValue = "1") int pageNum,
            @RequestParam(value = "is-read", required = false) String sortType){
        return new ResponseEntity(BaseResponse.of(historyService.getEveryNotice(pageNum, sortType), HttpStatus.OK), HttpStatus.OK);
    }

    @Xss
    @Auth
    @ApiOperation(value = "히스토리 - 삭제", notes = "사용자가 받은 알림에 대한 전체 내역에서 알림 삭제", authorizations = @Authorization(value = "Bearer +accessToken"))
    @PatchMapping(value = "/history")
    public ResponseEntity deleteNotice(@RequestParam("notice-id") List<Long> noticeList){
        if(historyService.deleteNotice(noticeList))
            return new ResponseEntity(BaseResponse.of("알림을 삭제했습니다.",  HttpStatus.OK), HttpStatus.OK);
        else
            return new ResponseEntity(BaseResponse.of("알림을 삭제하지 못했습니다.", HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);

    }

    @Xss
    @Auth
    @ApiOperation(value = "히스토리 - 삭제(실행취소)", notes = "알림 삭제에 대한 실행 취소", authorizations = @Authorization(value = "Bearer +accessToken"))
    @PatchMapping(value = "/history/undo")
    public ResponseEntity deleteNoticeUndo(@RequestParam("notice-id")List<Long> noticeList){
        if(historyService.deleteNoticeUndo(noticeList))
            return new ResponseEntity(BaseResponse.of("알림을 삭제를 취소했습니다.",  HttpStatus.OK), HttpStatus.OK);
        else
            return new ResponseEntity(BaseResponse.of("알림을 삭제 취소를 실패했습니다.", HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @Xss
    @Auth
    @ApiOperation(value = "히스토리 알림 읽음", notes = "사용자가 받은 알림에 대한 전체 내역에서 알림 읽음", authorizations = @Authorization(value = "Bearer +accessToken"))
    @PutMapping(value = "/history")
    public ResponseEntity noticeRead(@RequestParam(name = "notice-id") String noticeId){
        if(historyService.noticeRead(noticeId))
            return new ResponseEntity(BaseResponse.of("알림을 읽었습니다.",  HttpStatus.OK), HttpStatus.OK);
        else
            return new ResponseEntity(BaseResponse.of("알림을 읽지 못했습니다.", HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }
}
