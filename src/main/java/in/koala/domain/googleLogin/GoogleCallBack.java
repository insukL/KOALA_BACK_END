package in.koala.domain.googleLogin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleCallBack {
    private String code;
    private String error;
}
