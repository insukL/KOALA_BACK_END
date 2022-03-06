package in.koala.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.annotation.Nullable;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceToken {
    private Long id;
    private Long userId;
    @ApiModelProperty(hidden = true)
    private Long nonUserId;
    private String token;
    @ApiModelProperty(hidden = true)
    private Timestamp createdAt;
    @ApiModelProperty(hidden = true)
    private Timestamp updatedAt;

    public static DeviceToken ofNormalUser(Long userId, String token){
        DeviceToken deviceToken = new DeviceToken();

        deviceToken.userId = userId;
        deviceToken.token = token;

        return deviceToken;
    }

    public static DeviceToken ofNonUser(Long userId, @Nullable Long nonUserId, String token){
        DeviceToken deviceToken = new DeviceToken();

        deviceToken.userId = userId;
        deviceToken.nonUserId = nonUserId;
        deviceToken.token = token;

        return deviceToken;
    }
}
