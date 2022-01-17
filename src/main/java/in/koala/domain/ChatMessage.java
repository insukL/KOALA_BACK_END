package in.koala.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import in.koala.enums.ChatType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatMessage {
    private Long id;
    private Long sender;
    private String message;
    private ChatType type;
    private Timestamp sent_at;
    private Short is_deleted;
}
