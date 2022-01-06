package in.koala.controller.chat;

import in.koala.domain.ChatMessage;
import in.koala.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

//채팅 메시지 컨트롤러
//Jwt, 입퇴장 메시지 등등 기능 추가 필요
//해당 경로로 메시지를 보내면 해당 메소드를 실행시킨다.
//SimpMessagingTemplate로 내용을 보낸다.

@Controller
public class ChatController {
    @Resource
    private ChatService chatService;

    @MessageMapping(value = "/chat/message")
    public ResponseEntity send(ChatMessage message){
        chatService.send(message);
        return new ResponseEntity<String>("success", HttpStatus.OK);
    }
}
