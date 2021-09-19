package in.koala.controller;

import in.koala.domain.ChatMessage;
import in.koala.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ChatContorller {
    @Autowired
    private ChatService chatService;

    @MessageMapping("/chat/enter")
    public void enter(ChatMessage message){
        System.out.println(message.getSender() + "님이 입장하셨습니다.");
        chatService.enter(message);
    }

    @MessageMapping("/chat/exit")
    public void exit(ChatMessage message){
        System.out.println(message.getSender() + "님이 나가셨습니다.");
        chatService.exit(message);
    }

    @MessageMapping("/chat/message")
    public void send(ChatMessage message){
        System.out.println(message.getMessage());
        chatService.send(message);
    }

}
