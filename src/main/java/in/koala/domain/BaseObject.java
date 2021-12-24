package in.koala.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BaseObject {
    @ApiModelProperty(hidden = true)
    protected Long id;

    @ApiModelProperty(hidden = true)
    @JsonProperty("is_deleted")
    protected boolean is_deleted;

    @ApiModelProperty(hidden = true)
    protected Timestamp created_at;

    @ApiModelProperty(hidden = true)
    protected Timestamp updated_at;

}
