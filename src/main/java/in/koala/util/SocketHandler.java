package in.koala.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.koala.domain.ChatMessage;
import in.koala.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class SocketHandler extends TextWebSocketHandler {
    @Autowired
    private ChatService chatService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        chatService.join(session);
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender("관리자");
        chatMessage.setMessage("새로운 분이 들어오셨습니다");
        chatService.send(chatMessage);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        ChatMessage chatMessage = objectMapper.readValue(payload, ChatMessage.class);
        chatService.send(chatMessage);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
        chatService.exit(session);
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender("관리자");
        chatMessage.setMessage("누군가 나갔습니다");
        chatService.send(chatMessage);
    }
}
