package in.koala.serviceImpl;

import in.koala.domain.ChatMessage;
import in.koala.service.ChatService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ChatServiceImpl implements ChatService {
    @Resource
    private SimpMessagingTemplate template;

    @Value("${chat.room.id}")
    private String roomId;

    public void send(ChatMessage message){
        template.convertAndSend("/sub/" + roomId, message);
    }
}
