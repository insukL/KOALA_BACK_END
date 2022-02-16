package in.koala.controller.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import in.koala.domain.user.NormalUser;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ChangePasswordRequest {
    @NotNull(message="account 는 반드시 입력되야 합니다")
    @Length(message="계정은 1 ~ 15자 사이여야 합니다", min=1, max=15)
    private String account;

    @NotNull(message="패스워드는 반드시 입력해야 합니다")
    private String password;

    public NormalUser toEntity(){
        return NormalUser.builder()
                .account(account)
                .password(password)
                .build();
    }
}
