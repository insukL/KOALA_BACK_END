package in.koala.controller;

import in.koala.annotation.Auth;
import in.koala.annotation.ValidationGroups;
import in.koala.domain.Memo;
import in.koala.controller.response.BaseResponse;
import in.koala.service.MemoService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
public class MemoController {

    @Resource
    MemoService memoService;

    @Auth
    @ApiOperation(value = "보관함 메모 작성", notes = "보관함 메모 작성입니다.\n스크랩 id와 1~100자의 메모가 필요합니다.", authorizations = @Authorization(value = "Bearer +accessToken"))
    @PostMapping(value = "/memo")
    public ResponseEntity AddMemo(@Validated(ValidationGroups.createMemo.class) @RequestBody Memo memo) throws Exception {
        memoService.addMemo(memo);
        return new ResponseEntity(BaseResponse.of("메모가 작성되었습니다.", HttpStatus.OK), HttpStatus.OK);
    }

    @Auth
    @ApiOperation(value = "보관함 메모 조회", notes = "보관함 메모 조회입니다.", authorizations = @Authorization(value = "Bearer +accessToken"))
    @GetMapping(value = "/memo")
    public ResponseEntity getMemo() throws Exception {
        return new ResponseEntity(BaseResponse.of(memoService.getMemo(), HttpStatus.OK), HttpStatus.OK);
    }

    @Auth
    @ApiOperation(value = "보관함 메모 수정", notes = "보관함 메모 수정입니다.\n스크랩 id와 변경 메모가 필요합니다.", authorizations = @Authorization(value = "Bearer +accessToken"))
    @PatchMapping(value = "/memo")
    public ResponseEntity updateMemo(@Validated(ValidationGroups.createMemo.class) @RequestBody Memo memo) throws Exception {
        memoService.updateMemo(memo);
        return new ResponseEntity(BaseResponse.of("메모가 수정되었습니다.", HttpStatus.OK), HttpStatus.OK);
    }

}
