package in.koala.controller.dto;

import in.koala.domain.AuthEmail;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SendEmailRequest {
    @NotNull
    private String email;
    private String account;

    public AuthEmail toEntity(){
        return AuthEmail.builder()
                .email(email)
                .account(account)
                .build();
    }
}
