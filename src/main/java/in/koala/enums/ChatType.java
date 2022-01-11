package in.koala.enums;

import in.koala.domain.ChatMessage;
import in.koala.service.ChatService;

public enum ChatType {
    CHAT(1){
        public void setMessage(ChatService chatService, ChatMessage message){

        }
    },
    IMAGE(2){
        public void setMessage(ChatService chatService, ChatMessage message){

        }
    },
    ACCESS(3){
        public void setMessage(ChatService chatService, ChatMessage message){
            message.setMessage(chatService.getMemberCount().toString());
        }
    };

    Integer code;
    ChatType(Integer code){ this.code = code; }
    public Integer getCode(){return code;}
    public abstract void setMessage(ChatService chatService, ChatMessage message);
}
