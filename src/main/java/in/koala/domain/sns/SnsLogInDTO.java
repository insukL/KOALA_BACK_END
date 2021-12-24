package in.koala.domain.sns;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SnsLogInDTO {
    private String token;
    private String userIdentity;
}
