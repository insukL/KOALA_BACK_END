package in.koala.serviceImpl;

import com.nhncorp.lucy.security.xss.LucyXssFilter;
import com.nhncorp.lucy.security.xss.XssSaxFilter;
import in.koala.annotation.Auth;
import in.koala.domain.ChatMessage;
import in.koala.domain.Criteria;
import in.koala.domain.user.NormalUser;
import in.koala.enums.ChatType;
import in.koala.enums.ErrorMessage;
import in.koala.enums.FileType;
import in.koala.enums.TokenType;
import in.koala.exception.NonCriticalException;
import in.koala.mapper.ChatMessageMapper;
import in.koala.mapper.UserMapper;
import in.koala.service.ChatService;
import in.koala.service.UserService;
import in.koala.util.JwtUtil;
import in.koala.util.S3Util;
import in.koala.util.image.MultipartImage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;
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

    @Resource
    private S3Util s3Util;

    @Resource
    private UserService userService;

    @Value("${chat.room.id}")
    private String roomId;

    @Override
    public void send(Message<ChatMessage> message){
        String token = StompHeaderAccessor.wrap(message).getFirstNativeHeader("Authorization");
        LucyXssFilter xssFilter = XssSaxFilter.getInstance();
        Long id = Long.valueOf(String.valueOf(jwtUtil.getClaimsFromJwt(token, TokenType.ACCESS).get("id")));
        NormalUser user = userMapper.getNormalUserById(id)
                .orElseThrow(()->new NonCriticalException(ErrorMessage.USER_NOT_EXIST));

        ChatMessage chatMessage = message.getPayload();
        chatMessage.setSender(id);
        chatMessage.setMessage(xssFilter.doFilter(chatMessage.getMessage()));
        chatMessage.setSentAt(new Timestamp(new Date().getTime()));
        chatMessageMapper.insertMessage(chatMessage);

        chatMessage.setNickname(user.getNickname());
        chatMessage.setProfile(user.getProfile());
        template.convertAndSend("/sub/" + roomId, chatMessage);
    }

    @Override
    public String imageSend(MultipartFile multipartFile){
        NormalUser user = userService.getLoginNormalUserInfo();
        String url = s3Util.uploader(multipartFile, FileType.CHAT);
        ChatMessage message = ChatMessage.builder()
                                .sender(user.getId())
                                .nickname(user.getNickname())
                                .message(url)
                                .type(ChatType.IMAGE)
                                .profile(user.getProfile())
                                .sentAt(new Timestamp(new Date().getTime()))
                                .build();
        chatMessageMapper.insertMessage(message);
        template.convertAndSend("/sub/"+roomId, message);
        return "success";
    }

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
