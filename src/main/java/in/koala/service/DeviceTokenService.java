package in.koala.service;

import in.koala.domain.DeviceToken;

public interface DeviceTokenService {
    DeviceToken updateToken(String expiredToken, String newToken);
}
