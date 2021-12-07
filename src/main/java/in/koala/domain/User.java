package in.koala.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import in.koala.annotation.ValidationGroups;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    private Long id;

    @NotNull(message="account 는 반드시 입력되야 합니다", groups = {ValidationGroups.SingIn.class, ValidationGroups.Login.class, ValidationGroups.Password.class} )
    @Length(message="계정은 1 ~ 15자 사이여야 합니다", min=1, max=15, groups = {ValidationGroups.SingIn.class, ValidationGroups.Login.class} )
    private String account;

    private String password;

    @Email(message="이메일 형식에 맞지 않습니다", groups = {ValidationGroups.SingIn.class})
    @NotNull(message="이메일을 반드시 입력해야 합니다", groups = {ValidationGroups.SingIn.class})
    private String find_email;

    @Length(min=1, max=10, groups = {ValidationGroups.SingIn.class} )
    @Pattern(regexp = "^[a-zA-Z가-힣0-9]{1,20}$", groups = {ValidationGroups.SingIn.class} , message = "닉네임의 특수문자와 초성은 사용불가능합니다")
    private String nickname;

    @ApiModelProperty(hidden = true)
    private String sns_email;
    @ApiModelProperty(hidden = true)
    private String profile;
    @ApiModelProperty(hidden = true)
    private Short user_type;
    @ApiModelProperty(hidden = true)
    private Short is_auth;
    @ApiModelProperty(hidden = true)
    private Timestamp created_at;
    @ApiModelProperty(hidden = true)
    private Timestamp updated_at;

    @Builder
    public User(String account, String sns_email, String nickname, String profile, Short user_type) {
        this.account = account;
        this.sns_email = sns_email;
        this.nickname = nickname;
        this.profile = profile;
        this.user_type = user_type;
    }
}
