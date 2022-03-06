package in.koala.controller.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import in.koala.domain.AuthEmail;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SendEmailRequest {
    @NotNull
    private String email;
    private String secret;
    private String account;

    public AuthEmail toEntity(){
        return AuthEmail.builder()
                .email(email)
                .account(account)
                .secret(secret)
                .build();
    }
}
