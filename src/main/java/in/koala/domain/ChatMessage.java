package in.koala.domain;

import in.koala.enums.ChatType;

public class ChatMessage {
    private String sender;
    private String message;
    private ChatType type;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ChatType getType() { return type; }

    public void setType(ChatType type) { this.type = type; }
}
