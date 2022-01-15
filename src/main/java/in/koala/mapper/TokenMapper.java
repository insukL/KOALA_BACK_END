package in.koala.mapper;

import in.koala.domain.DeviceToken;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenMapper {
    void insertDeviceTokenNonUser(Long nonUserId, String deviceToken);
    void updateUserId(Long userId, String token);
    void updateToken(Long userId, String token);
    void updateTokenByNonUserId(Long nonUserId, String token);
    void insertDeviceToken(Long userId, String token);
    Optional<DeviceToken> getTokenByDeviceToken(String deviceToken);
    void updateExTokenToNewToken(String expiredToken, String newToken);
}
