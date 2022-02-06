package in.koala.service;

import in.koala.domain.ChatMessage;
import in.koala.domain.Criteria;
import org.springframework.messaging.Message;

import java.util.List;

public interface ChatService {
    void send(Message<ChatMessage> message);
    void imageSend();
    String getMemberCount();
    String getMemberName(Long id);
    List<ChatMessage> getMessageList(Criteria criteria);
    List<ChatMessage> searchMessageList(Criteria criteria, String word);
}
