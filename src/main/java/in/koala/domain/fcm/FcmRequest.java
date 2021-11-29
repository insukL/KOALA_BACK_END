package in.koala.domain.fcm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FcmRequest {
    @JsonProperty("message")
    private FcmMessage message;

    public FcmRequest(FcmMessage message){
        this.message = message;
    }
}
