package in.koala.domain.fcm;

import com.fasterxml.jackson.annotation.JsonProperty;
import in.koala.domain.fcm.data.Message;
import in.koala.domain.fcm.data.Notification;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
@Getter
public class FcmRequest {

    @JsonProperty("validate_only")
    private boolean validateOnly;
    @JsonProperty("message")
    private Message message;

    public FcmRequest(String token, String title, String body){
        Message message = new Message();
        Notification notification = new Notification();

        notification.setBody(body);
        notification.setTitle(title);

        message.setToken(token);
        message.setNotification(notification);

        this.message = message;
        this.validateOnly = false;
    }
}

