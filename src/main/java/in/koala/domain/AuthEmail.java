package in.koala.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import in.koala.annotation.ValidationGroups;
import in.koala.enums.EmailType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthEmail {
    private Long id;

    private String account;

    @ApiModelProperty(hidden = true)
    private Long user_id;

    @ApiModelProperty(hidden = true)
    private EmailType type;

    @Email
    private String email;

    private String secret;

    @ApiModelProperty(hidden = true)
    private Timestamp created_at;
    @ApiModelProperty(hidden = true)
    private Timestamp expired_at;
}
