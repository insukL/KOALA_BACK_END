package in.koala.controller.chat;

import in.koala.annotation.Auth;
import in.koala.domain.BanWord;
import in.koala.controller.response.BaseResponse;
import in.koala.service.ChatBanService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/chat/ban")
public class ChatBanController {
    private final ChatBanService chatBanService;

    @Auth
    @ApiOperation(value = "금칙어 추가", notes = "금칙어를 추가합니다. 1 ~ 20 사이의 word가 필요합니다.", authorizations = @Authorization(value = "Bearer +accessToken"))
    @PostMapping(value="/word")
    public ResponseEntity addBanWord(@Valid @RequestBody BanWord banWord) throws Exception{
        chatBanService.addBanWord(banWord);
        return new ResponseEntity(BaseResponse.of("금칙어를 추가했습니다.", HttpStatus.OK), HttpStatus.OK);
    }

    @Auth
    @ApiOperation(value = "금칙어 조회", notes = "로그인된 유저의 모든 금칙어를 조회합니다. 없을 경우 빈 리스트가 반환됩니다.", authorizations = @Authorization(value = "Bearer +accessToken"))
    @GetMapping(value="/word")
    public ResponseEntity getBanWord() throws Exception {
        return new ResponseEntity (BaseResponse.of(chatBanService.getBanWordList(), HttpStatus.OK), HttpStatus.OK);
    }

    @Auth
    @ApiOperation(value = "금칙어 수정", notes = "특정 금칙어를 수정합니다. \n수정할 금칙어 id와 1 ~ 20 사이의 word가 필요합니다.", authorizations = @Authorization(value = "Bearer +accessToken"))
    @PutMapping(value="/word")
    public ResponseEntity updateBanWord(@Valid @RequestBody BanWord banWord) throws Exception {
        chatBanService.updateBanWord(banWord);
        return new ResponseEntity(BaseResponse.of("금칙어를 수정했습니다.", HttpStatus.OK), HttpStatus.OK);
    }

    @Auth
    @ApiOperation(value = "금칙어 삭제", notes = "특정 금칙어를 삭제합니다. 삭제할 금칙어 id가 필요합니다.", authorizations = @Authorization(value = "Bearer +accessToken"))
    @DeleteMapping(value="/word")
    public ResponseEntity deleteBanWord(@RequestParam("id") Long id) throws Exception {
        chatBanService.deleteBanWord(id);
        return new ResponseEntity(BaseResponse.of("금칙어를 삭제했습니다.", HttpStatus.OK), HttpStatus.OK);
    }

}
