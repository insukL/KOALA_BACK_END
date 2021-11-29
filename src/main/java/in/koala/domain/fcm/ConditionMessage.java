package in.koala.domain.fcm;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ConditionMessage extends FcmMessage{
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("condition")
    private String condition;

    public ConditionMessage(String title, String body, String condition){
        super(title, body);
        this.condition = condition;
    }

    public ConditionMessage(FcmNotification notification, String condition){
        super(notification);
        this.condition = condition;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
