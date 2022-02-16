package in.koala.domain.user;

import in.koala.annotation.ValidationGroups;
import in.koala.enums.SnsType;
import in.koala.enums.UserType;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter @Setter @ToString
@NoArgsConstructor
public class NormalUser extends User{
    private String account;
    private String password;
    private String findEmail;
    private String nickname;
    private String snsEmail;
    private String profile;
    private Short isAuth;
    private SnsType snsType;

    @Builder
    public NormalUser(String account, String password, String findEmail, String nickname, String snsEmail, String profile, Short isAuth, SnsType snsType, UserType userType) {
        this.account = account;
        this.password = password;
        this.findEmail = findEmail;
        this.nickname = nickname;
        this.snsEmail = snsEmail;
        this.profile = profile;
        this.isAuth = isAuth;
        this.snsType = snsType;
        this.userType = userType;
    }
}
