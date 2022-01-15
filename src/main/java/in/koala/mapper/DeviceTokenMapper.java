package in.koala.mapper;

import in.koala.domain.DeviceToken;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceTokenMapper {
    void insertDeviceToken(DeviceToken deviceToken);
    void updateUserId(DeviceToken deviceToken);
    int checkTokenExist(String deviceToken);
    void updateUserAndNonUserId(DeviceToken deviceToken);

    void updateTokenByNonUserId(Long nonUserId, String token);
    Optional<DeviceToken> getTokenByDeviceToken(String deviceToken);
    void updateExTokenToNewToken(String expiredToken, String newToken);
}
