package in.koala.controller;

import in.koala.domain.Memo;
import in.koala.service.MemoService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
public class MemoController {

    @Resource
    MemoService memoService;

    @ApiOperation(value = "메모 작성", notes = "보관함 메모 작성 api 입니다.")
    @PostMapping(value = "/memo")
    public ResponseEntity AddMemo(@RequestBody Memo memo) throws Exception {
        memoService.addMemo(memo);
        return new ResponseEntity("메모가 작성되었습니다.", HttpStatus.CREATED);
    }

    @ApiOperation(value = "메모 삭제", notes = "보관함 메모 삭제 api 입니다.")
    @DeleteMapping(value = "/memo")
    public ResponseEntity deleteMemo(@RequestBody Memo memo) throws Exception {
        memoService.deleteMemo(memo);
        return new ResponseEntity("메모가 삭제되었습니다.", HttpStatus.OK);
    }

    @ApiOperation(value = "메모 수정", notes = "보관함 메모 수정 api 입니다.")
    @PatchMapping(value = "/memo")
    public ResponseEntity updateMemo(@RequestBody Memo memo) throws Exception {
        memoService.updateMemo(memo);
        return new ResponseEntity("메모가 수정되었습니다.", HttpStatus.OK);
    }

    @ApiOperation(value = "메모 조회", notes = "보관함 메모 조회 api 입니다.")
    @GetMapping(value = "/memo/{user_scrap_id}")
    public ResponseEntity getMemo(@RequestParam Long user_scrap_id) throws Exception {
        return new ResponseEntity(memoService.getMemo(user_scrap_id), HttpStatus.OK);
    }

}
