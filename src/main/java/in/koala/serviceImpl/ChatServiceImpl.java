package in.koala.serviceImpl;

import in.koala.domain.ChatMessage;
import in.koala.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ChatServiceImpl implements ChatService {
    @Autowired
    SimpMessagingTemplate template;

    public void enter(ChatMessage message){
        message.setMessage(message.getSender() + "님이 입장하셨습니다.");
        template.convertAndSend("/sub/chat/room", message);
    }

    public void exit(ChatMessage message){
        message.setMessage(message.getSender() + "님이 나가셨습니다.");
        template.convertAndSend("/sub/chat/room", message);
    }

    public void send(ChatMessage message){
        template.convertAndSend("/sub/chat/room", message);
    }

}
