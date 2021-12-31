package in.koala.domain.fcm;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TopicMessage extends FcmMessage{
    @JsonProperty("topic")
    private String topic;

    public TopicMessage(String title, String body, String url, String topic){
        super(title, body, url);
        this.topic = topic;
    }

    public TopicMessage(FcmNotification notification, String url, String topic){
        super(notification, url);
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
