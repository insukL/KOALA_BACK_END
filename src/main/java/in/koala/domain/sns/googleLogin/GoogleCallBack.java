package in.koala.domain.sns.googleLogin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleCallBack {
    private String code;
    private String error;
}
