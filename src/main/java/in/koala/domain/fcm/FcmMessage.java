package in.koala.domain.fcm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class FcmMessage{
    @JsonProperty("notification")
    private FcmNotification notification;

    @JsonProperty("data")
    private Data data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("webpush")
    private WebPushConfig webPushConfig;

    public FcmMessage(String title, String body, String url){
        notification = new FcmNotification(title, body);
        data = new Data(url);
    }

    public FcmMessage(FcmNotification notification, String url){
        this.notification = notification;
        this.data = new Data(url);
        this.webPushConfig = new WebPushConfig(url);
    }

    public FcmNotification getNotification() {
        return notification;
    }

    public void setNotification(FcmNotification notification) {
        this.notification = notification;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public WebPushConfig getWebPushConfig() {
        return webPushConfig;
    }

    public void setWebPushConfig(WebPushConfig webPushConfig) {
        this.webPushConfig = webPushConfig;
    }

    public void setTitle(String title){
        this.notification.setTitle(title);
    }

    public void setBody(String body){
        this.notification.setBody(body);
    }

    public void setUrl(String url){
        this.data.setUrl(url);
        this.webPushConfig.getOptions().setLink(url);
    }

    @JsonIgnore
    public String getTitle(){
        return this.notification.getTitle();
    }

    @JsonIgnore
    public String getBody(){
        return this.notification.getBody();
    }

    @JsonIgnore
    public String getUrl(){ return this.data.getUrl(); }
}