package in.koala.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FcmRequest {
    @JsonProperty("message")
    private FcmMessage message;

    public FcmRequest(String title, String body){
        message = new FcmMessage(title, body);
    }

    public void setToken(String token){
        message.setToken(token);
    }

    public void setTopic(String topic){
        message.setTopic(topic);
    }

    public void setCondition(String condition){
        message.setCondition(condition);
    }

    public class FcmMessage{
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("token")
        private String token;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("topic")
        private String topic;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("condition")
        private String condition;

        @JsonProperty("notification")
        private FcmNotification notification;

        public FcmMessage(String title, String body){
            notification = new FcmNotification(title, body);
        }

        public void setToken(String token) {
            this.token = token;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public void setCondition(String condition){
            this.condition = condition;
        }
    }

    public class FcmNotification {
        @JsonProperty("title")
        private String title;
        @JsonProperty("body")
        private String body;

        public FcmNotification(String title, String body){
            this.title = title;
            this.body = body;
        }
    }
}
