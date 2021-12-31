package in.koala.domain.fcm;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ConditionMessage extends FcmMessage{
    @JsonProperty("condition")
    private String condition;

    public ConditionMessage(String title, String body, String url, String condition){
        super(title, body, url);
        this.condition = condition;
    }

    public ConditionMessage(FcmNotification notification, String url, String condition){
        super(notification, url);
        this.condition = condition;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
