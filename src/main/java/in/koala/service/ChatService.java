package in.koala.service;

import in.koala.domain.ChatMessage;
import in.koala.domain.Criteria;
import in.koala.domain.user.NormalUser;
import org.springframework.messaging.Message;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ChatService {
    void send(Message<ChatMessage> message);
    String imageSend(MultipartFile multipartFile);
    String getMemberCount();
    String getMemberName(Long id);
    List<ChatMessage> getMessageList(Criteria criteria);
    List<ChatMessage> searchMessageList(Criteria criteria, String word);
}
