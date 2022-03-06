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
public class AuthEmail {
    private Long id;
    private String account;
    private Long userId;
    private EmailType type;
    private String email;
    private String secret;
    private Timestamp createdAt;
    private Timestamp expiredAt;

    @Builder
    public AuthEmail(String account, Long userId, EmailType type, String email, String secret) {
        this.account = account;
        this.userId = userId;
        this.type = type;
        this.email = email;
        this.secret = secret;
    }
}
