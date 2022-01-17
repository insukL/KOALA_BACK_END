package in.koala.controller.chat;

import in.koala.annotation.Auth;
import in.koala.domain.ChatReport;
import in.koala.domain.response.CustomBody;
import in.koala.service.ChatReportService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;

@Controller
public class ChatReportController {
    @Resource
    ChatReportService chatReportService;

    @Auth
    @PostMapping(value = "/report")
    @ApiOperation(value="채팅 신고", notes="특정 채팅 신고 API", authorizations = @Authorization(value = "Bearer +accessToken"))
    public ResponseEntity report(@RequestBody ChatReport chatReport){
        chatReportService.reportChat(chatReport);
        return new ResponseEntity(CustomBody.of("success", HttpStatus.OK), HttpStatus.OK);
    }
}
