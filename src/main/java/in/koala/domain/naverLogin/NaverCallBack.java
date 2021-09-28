package in.koala.domain.naverLogin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NaverCallBack {
    private String code;
    private String state;
    private String error;
    private String error_description;
}
