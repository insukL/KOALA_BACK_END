package in.koala.service;

import in.koala.domain.ChatMessage;

public interface ChatService {
    void enter(ChatMessage message);
    void exit(ChatMessage message);
    void send(ChatMessage message);
}
