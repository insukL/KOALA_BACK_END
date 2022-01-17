package in.koala.service;

import com.amazonaws.services.ecs.model.Device;
import in.koala.domain.DeviceToken;

public interface DeviceTokenService {
    DeviceToken updateToken(String expiredToken, String newToken);
    void updateTokenTableUserId(DeviceToken deviceToken);
    void updateTokenTableNonUserId(DeviceToken deviceToken);
    DeviceToken getDeviceTokenInfoByDeviceToken(String deviceToken);
    boolean checkTokenExist(String deviceToken);
    void insertDeviceToken(DeviceToken deviceToken);
}
