package in.koala.domain.fcm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class FcmMessage{
    @JsonProperty("notification")
    private FcmNotification notification;

    public FcmMessage(String title, String body){
        notification = new FcmNotification(title, body);
    }

    public FcmMessage(FcmNotification notification){
        this.notification = notification;
    }

    public void setTitle(String title){
        this.notification.setTitle(title);
    }

    public void setBody(String body){
        this.notification.setBody(body);
    }

    @JsonIgnore
    public String getTitle(){
        return this.notification.getTitle();
    }

    @JsonIgnore
    public String getBody(){
        return this.notification.getBody();
    }
}