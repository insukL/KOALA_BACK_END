package in.koala.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthEmail {
    private Long id;
    private Long userId;
    private Short type;
    private String email;
    private String secret;
}
