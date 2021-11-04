package in.koala.domain.fcm.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class Message {

    @JsonProperty("notification")
    private Notification notification;
    @JsonProperty("token")
    private String token;
}

