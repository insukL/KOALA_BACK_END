package in.koala.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import in.koala.annotation.ValidationGroups;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    private Long id;
    @NotNull(message="account 는 반드시 입력되야 합니다", groups = ValidationGroups.Create.class)
    private String account;
    private String password;
    private String find_email;
    private String sns_email;
    private String nickname;
    private String profile;
    private Short user_type;
    private Short is_auth;
    private Timestamp created_at;
    private Timestamp updated_at;

    @Builder
    public User(String account, String sns_email, String nickname, String profile, Short user_type) {
        this.account = account;
        this.sns_email = sns_email;
        this.nickname = nickname;
        this.profile = profile;
        this.user_type = user_type;
    }
}
