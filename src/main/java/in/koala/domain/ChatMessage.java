package in.koala.domain;

import in.koala.enums.ChatType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChatMessage {
    private String sender;
    private String message;
    private ChatType type;

}
