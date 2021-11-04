package in.koala.domain.fcm.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class Notification {

    @JsonProperty("title")
    private String title;
    @JsonProperty("body")
    private String body;
    @JsonProperty("image")
    private String image = "https://dev-moye-designerpage.s3.ap-northeast-2.amazonaws.com/moye/moye_profile.png";
}

