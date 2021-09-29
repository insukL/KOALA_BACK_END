package in.koala.domain.kakaoLogin;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class KakaoCallBack {
    private String code;
    private String state;
    private String error;
    private String error_description;
}
