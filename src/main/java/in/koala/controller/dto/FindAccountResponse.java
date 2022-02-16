package in.koala.controller.dto;

import lombok.Data;

@Data
public class FindAccountResponse {
    String account;

    public FindAccountResponse(String account) {
        this.account = account;
    }
}
