package in.koala.controller.chat;

import in.koala.annotation.Auth;
import in.koala.domain.ChatMessage;
import in.koala.domain.Criteria;
import in.koala.controller.response.BaseResponse;
import in.koala.domain.user.NormalUser;
import in.koala.enums.UserType;
import in.koala.service.ChatService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Auth(role = UserType.NORMAL)
    @ResponseBody
    @GetMapping(value = "/chat")
    @ApiOperation(value="채팅 리스트", notes="채팅 리스트를 얻는 API", authorizations = @Authorization(value = "Bearer +accessToken"))
    public ResponseEntity getChattingList(@ModelAttribute Criteria criteria) throws Exception{
        return new ResponseEntity(BaseResponse.of(chatService.getMessageList(criteria), HttpStatus.OK), HttpStatus.OK);
    }

    @Auth(role = UserType.NORMAL)
    @ResponseBody
    @GetMapping(value = "/chat/search")
    @ApiOperation(value="채팅 검색", notes="채팅 메세지를 검색하는 API", authorizations = @Authorization(value = "Bearer +accessToken"))
    public ResponseEntity findChatting(@ModelAttribute Criteria criteria, @RequestParam String word) throws Exception{
        return new ResponseEntity(BaseResponse.of(chatService.searchMessageList(criteria, word), HttpStatus.OK), HttpStatus.OK);
    }

    @Auth(role = UserType.NORMAL)
    @ResponseBody
    @PostMapping(value = "/chat/image")
    @ApiOperation(value="이미지 업로드", notes="채팅 이미지 업로드", authorizations = @Authorization(value = "Bearer +accessToken"))
    public ResponseEntity uploadImage(@RequestBody MultipartFile file) throws Exception{
        return new ResponseEntity(BaseResponse.of(chatService.imageSend(file), HttpStatus.OK), HttpStatus.OK);
    }
}
