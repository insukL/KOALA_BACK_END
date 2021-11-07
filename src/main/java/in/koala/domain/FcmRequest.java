package in.koala.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class FcmRequest {
    private HashMap<String, Object> message;

    public FcmRequest(String token, String title, String body){
        HashMap<String, Object> notification = new HashMap<String, Object>(){
            {
                put("title", title);
                put("body", body);
            }
        };

        this.message = new HashMap<String, Object>(){
            {
                put("token", token);
                put("notification", notification);
            }
        };
    }
}
