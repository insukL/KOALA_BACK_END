package in.koala.domain.fcm;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenMessage extends FcmMessage{
    @JsonProperty("token")
    private String token;

    public TokenMessage(String title, String body, String url, String token){
        super(title, body, url);
        this.token = token;
    }

    public TokenMessage(FcmNotification notification, String url, String token){
        super(notification, url);
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
