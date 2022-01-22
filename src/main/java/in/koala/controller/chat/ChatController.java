package in.koala.controller.chat;

import in.koala.domain.ChatMessage;
import in.koala.domain.Criteria;
import in.koala.domain.response.CustomBody;
import in.koala.service.ChatService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

//채팅 메시지 컨트롤러
//해당 경로로 메시지를 보내면 해당 메소드를 실행시킨다.
//SimpMessagingTemplate로 내용을 보낸다.

@Controller
public class ChatController {
    @Resource
    private ChatService chatService;

    @MessageMapping(value = "/chat/message")
    public ResponseEntity send(Message<ChatMessage> message){
        chatService.send(message);
        return new ResponseEntity<String>("success", HttpStatus.OK);
    }

    @MessageMapping(value = "/chat/member")
    public ResponseEntity getMemberCount(){
        return new ResponseEntity<String>(chatService.getMemberCount(), HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = "/chat")
    @ApiOperation(value="채팅 리스트", notes="채팅 리스트를 얻는 API", authorizations = @Authorization(value = "Bearer +accessToken"))
    public ResponseEntity getChattingList(@ModelAttribute Criteria criteria) throws Exception{
        return new ResponseEntity(CustomBody.of(chatService.getMessageList(criteria), HttpStatus.OK), HttpStatus.OK);
    }
}
