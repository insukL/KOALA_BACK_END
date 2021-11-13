package in.koala.domain;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
public class User {
    private Long id;
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
        this.is_auth = is_auth;
    }
}
