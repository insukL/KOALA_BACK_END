package in.koala.domain.fcm;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;

public class FcmNotification {
    @JsonProperty("title")
    private String title;

    @JsonProperty("body")
    private String body;

    @Value("${alarm.image.url}")
    @JsonProperty("image")
    private String image;

    public FcmNotification(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getImage(){ return image; }

    public void setImage(String image){ this.image = image; }
}
