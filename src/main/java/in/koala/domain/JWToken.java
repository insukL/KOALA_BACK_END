package in.koala.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class JWToken {
    private String accessToken;
    private String refreshToken;
    private String socketToken;

    public static JWToken ofLogin(String accessToken, String refreshToken){
        JWToken jwToken = new JWToken();

        jwToken.accessToken = accessToken;
        jwToken.refreshToken = refreshToken;

        return jwToken;
    }

    public static JWToken ofChat(String socketToken){
        JWToken jwToken = new JWToken();

        jwToken.socketToken = socketToken;

        return jwToken;
    }
}
