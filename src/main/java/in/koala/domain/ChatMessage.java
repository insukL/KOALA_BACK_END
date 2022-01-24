package in.koala.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import in.koala.enums.ChatType;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatMessage {
    private Long id;
    private Long sender;
    private String message;
    private ChatType type;
    private Timestamp sentAt;
    private Boolean isDeleted;
    private String nickname;
}
