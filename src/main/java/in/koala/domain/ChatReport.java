package in.koala.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ChatReport {
    private Long id;
    private Long userId;
    private Long messageId;
    private String reason;
    @ApiModelProperty(hidden = true)
    private boolean isDeleted;
    @ApiModelProperty(hidden = true)
    private Timestamp createdAt;
    @ApiModelProperty(hidden = true)
    private Timestamp updatedAt;
}
