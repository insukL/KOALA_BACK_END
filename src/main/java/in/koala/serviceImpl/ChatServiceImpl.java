package in.koala.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.koala.domain.ChatMessage;
import in.koala.service.ChatService;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Service
public class ChatServiceImpl implements ChatService {
    private Set<WebSocketSession> sessionList = new HashSet<>();
    private ObjectMapper objectMapper = new ObjectMapper();

    public void join(WebSocketSession session){
        sessionList.add(session);
    }

    public void exit(WebSocketSession session){
        sessionList.remove(session);
    }

    public void send(ChatMessage chatMessage){
        try {
            TextMessage message = new TextMessage(objectMapper.writeValueAsString(chatMessage));
            sessionList.parallelStream().forEach(session -> {
                try {
                    session.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
