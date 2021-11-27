package in.koala.domain.Fcm;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TopicMessage extends FcmMessage{
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("topic")
    private String topic;

    public TopicMessage(String title, String body, String topic){
        super(title, body);
        this.topic = topic;
    }

    public TopicMessage(FcmNotification notification, String topic){
        super(notification);
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
