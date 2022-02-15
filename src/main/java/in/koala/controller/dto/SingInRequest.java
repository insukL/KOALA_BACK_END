package in.koala.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import in.koala.annotation.ValidationGroups;
import in.koala.domain.user.NormalUser;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SingInRequest {
    @NotNull(message="account 는 반드시 입력되야 합니다")
    @Length(message="계정은 1 ~ 15자 사이여야 합니다", min=1, max=15)
    private String account;

    @Email(message="이메일 형식에 맞지 않습니다")
    @NotNull(message="이메일을 반드시 입력해야 합니다")
    private String findEmail;

    @NotNull(message="패스워드는 반드시 입력해야 합니다")
    private String password;

    @Length(min=1, max=10)
    @Pattern(regexp = "^[a-zA-Z가-힣0-9]{1,20}$", message = "닉네임의 특수문자와 초성은 사용불가능합니다")
    @NotNull(message ="닉네임을 반드시 입력해야 합니다")
    private String nickname;

    public NormalUser toEntity(){
        return NormalUser.builder()
                .account(account)
                .findEmail(findEmail)
                .password(password)
                .nickname(nickname)
                .build();
    }
}
