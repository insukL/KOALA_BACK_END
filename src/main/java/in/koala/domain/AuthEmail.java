package in.koala.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import in.koala.annotation.ValidationGroups;
import in.koala.enums.EmailType;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Getter
@Setter
@ToString
@NoArgsConstructor
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

    @Builder
    public AuthEmail(String account, Long user_id, EmailType type, String email, String secret) {
        this.account = account;
        this.user_id = user_id;
        this.type = type;
        this.email = email;
        this.secret = secret;
    }
}
