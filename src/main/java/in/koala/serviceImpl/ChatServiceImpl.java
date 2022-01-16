package in.koala.serviceImpl;

import in.koala.domain.ChatMessage;
import in.koala.enums.ChatType;
import in.koala.service.ChatService;
import in.koala.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ChatServiceImpl implements ChatService {
    @Resource
    private SimpMessagingTemplate template;

    @Resource(name="redisTemplate")
    private SetOperations<String, String> setOps;

    @Resource
    private JwtUtil jwtUtil;

    @Value("${chat.room.id}")
    private String roomId;

    @Override
    public void send(ChatMessage message){
        message.getType().setMessage(this, message);
        template.convertAndSend("/sub/" + roomId, message);
    }

    @Override
    public void imageSend(){}

    @Override
    public String getMemberCount(){
        ChatMessage message = ChatMessage.builder()
                                .sender("server")
                                .message(setOps.size("member").toString())
                                .type(ChatType.ACCESS)
                                .build();
        template.convertAndSend("/sub/" + roomId, message);
        return message.getMessage();
    }

}
