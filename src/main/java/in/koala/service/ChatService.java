package in.koala.service;

import in.koala.domain.ChatMessage;
import org.springframework.web.socket.WebSocketSession;

public interface ChatService {
    void join(WebSocketSession session);
    void exit(WebSocketSession session);
    void send(ChatMessage message);
}
