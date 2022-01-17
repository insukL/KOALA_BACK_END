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
    private Long user_id;
    @ApiModelProperty(hidden = true)
    private Long non_user_id;
    private String token;
    @ApiModelProperty(hidden = true)
    private Timestamp created_at;
    @ApiModelProperty(hidden = true)
    private Timestamp updated_at;

    public static DeviceToken ofNormalUser(Long userId, String token){
        DeviceToken deviceToken = new DeviceToken();

        deviceToken.user_id = userId;
        deviceToken.token = token;

        return deviceToken;
    }

    public static DeviceToken ofNonUser(Long userId, @Nullable Long nonUserId, String token){
        DeviceToken deviceToken = new DeviceToken();

        deviceToken.user_id = userId;
        deviceToken.non_user_id = nonUserId;
        deviceToken.token = token;

        return deviceToken;
    }
}
