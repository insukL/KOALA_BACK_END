package in.koala.serviceImpl;

import in.koala.domain.ChatMessage;
import in.koala.domain.Criteria;
import in.koala.domain.user.NormalUser;
import in.koala.enums.ChatType;
import in.koala.enums.ErrorMessage;
import in.koala.enums.TokenType;
import in.koala.exception.NonCriticalException;
import in.koala.mapper.ChatMessageMapper;
import in.koala.mapper.UserMapper;
import in.koala.service.ChatService;
import in.koala.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {
    @Resource
    private SimpMessagingTemplate template;

    @Resource(name="redisTemplate")
    private SetOperations<String, String> setOps;

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private UserMapper userMapper;

    @Resource
    private ChatMessageMapper chatMessageMapper;

    @Value("${chat.room.id}")
    private String roomId;

    @Override
    public void send(Message<ChatMessage> message){
        String token = StompHeaderAccessor.wrap(message).getFirstNativeHeader("Authorization");
        jwtUtil.validateToken(token, TokenType.SOCKET);
        Long id = Long.valueOf(String.valueOf(jwtUtil.getClaimFromJwt(token).get("id")));

        ChatMessage chatMessage = message.getPayload();
        chatMessage.setSender(id);
        chatMessageMapper.insertMessage(chatMessage);
        NormalUser user = userMapper.getNormalUserById(id)
                .orElseThrow(()->new NonCriticalException(ErrorMessage.USER_NOT_EXIST));
        chatMessage.setNickname(user.getNickname());
        chatMessage.setProfile(user.getProfile());
        template.convertAndSend("/sub/" + roomId, chatMessage);
    }

    @Override
    public void imageSend(){}

    @Override
    public String getMemberCount(){
        ChatMessage message = ChatMessage.builder()
                                .sender(Long.valueOf(0))
                                .message(setOps.size("member").toString())
                                .type(ChatType.ACCESS)
                                .build();
        template.convertAndSend("/sub/" + roomId, message);
        return message.getMessage();
    }

    @Override
    public String getMemberName(Long id){
        return userMapper.getNormalUserById(id)
                .orElseThrow(()->new NonCriticalException(ErrorMessage.USER_NOT_EXIST))
                .getNickname();
    }

    @Override
    public List<ChatMessage> getMessageList(Criteria criteria){
        return chatMessageMapper.getMessageList(criteria);
    }

    @Override
    public List<ChatMessage> searchMessageList(Criteria criteria, String word){
        return chatMessageMapper.searchMessage(criteria, word);
    }
}
