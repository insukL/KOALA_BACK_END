package in.koala.domain.fcm;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Data {
    @JsonProperty("url")
    private String url;

    public Data(String url){ this.url = url; }

    public String getUrl(){ return url; }

    public void setUrl(String url){ this.url = url; }
}
