package in.koala.serviceImpl;

import in.koala.domain.DeviceToken;
import in.koala.domain.User;
import in.koala.enums.ErrorMessage;
import in.koala.exception.NonCriticalException;
import in.koala.mapper.TokenMapper;
import in.koala.service.DeviceTokenService;
import in.koala.service.UserService;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class DeviceTokenServiceImpl implements DeviceTokenService {

    private final UserService userService;
    private final TokenMapper tokenMapper;


    @Override
    public DeviceToken updateToken(String expiredToken, String newToken) {
        DeviceToken deviceToken = tokenMapper.getTokenByDeviceToken(expiredToken)
                .orElseThrow(() -> new NonCriticalException(ErrorMessage.DEVICE_TOKEN_NOT_EXIST));

        tokenMapper.updateExTokenToNewToken(expiredToken, newToken);

        deviceToken.setToken(newToken);

        return deviceToken;
    }
}
