package in.koala.service;

import in.koala.domain.ChatMessage;

public interface ChatService {
    void send(ChatMessage message);

}
