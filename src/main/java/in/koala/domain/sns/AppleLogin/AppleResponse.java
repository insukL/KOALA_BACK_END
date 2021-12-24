package in.koala.domain.sns.AppleLogin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppleResponse {
    private String state;
    private String code;
    private String id_token;
    private String user;
}
