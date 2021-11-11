package in.koala.domain;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class AuthEmail {
    private Long id;
    private String account;
    private Long user_id;
    private Short type;
    private String email;
    private String secret;
    private Timestamp created_at;
    private Timestamp expired_at;
}
