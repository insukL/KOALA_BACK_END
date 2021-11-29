package in.koala.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import in.koala.annotation.ValidationGroups;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthEmail {
    private Long id;
    private String account;
    private Long user_id;
    @NotNull
    private Short type;
    @Email
    private String email;
    private String secret;
    @ApiModelProperty(hidden = true)
    private Timestamp created_at;
    @ApiModelProperty(hidden = true)
    private Timestamp expired_at;
}
