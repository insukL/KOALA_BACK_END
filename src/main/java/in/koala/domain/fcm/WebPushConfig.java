package in.koala.domain.fcm;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WebPushConfig {
    @JsonProperty("fcm_options")
    FcmOptions options;

    public WebPushConfig(String link){ options = new FcmOptions(link); }
    public FcmOptions getOptions(){ return options; }
    public void setOptions(FcmOptions options){ this.options = options; }

    class FcmOptions{
        private String link;

        public FcmOptions(String link){
            this.link = link;
        }

        public String getLink(){ return link; }
        public void setLink(String link){ this.link = link; }
    }
}
