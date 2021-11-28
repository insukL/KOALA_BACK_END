package in.koala.domain.fcm;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenMessage extends FcmMessage{
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("token")
    private String token;

    public TokenMessage(String title, String body, String token){
        super(title, body);
        this.token = token;
    }

    public TokenMessage(FcmNotification notification, String token){
        super(notification);
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
