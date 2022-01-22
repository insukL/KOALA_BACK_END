package in.koala.enums;

import in.koala.domain.ChatMessage;
import in.koala.service.ChatService;

public enum ChatType {
    CHAT(0),
    IMAGE(1),
    ACCESS(2)
    ;

    Integer code;
    ChatType(Integer code){ this.code = code; }
    public Integer getCode(){return code;}
    public void setMessage(ChatService chatService, ChatMessage message){ return;}
}
