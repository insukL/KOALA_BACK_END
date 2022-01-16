package in.koala.serviceImpl;

import in.koala.domain.DeviceToken;
import in.koala.enums.ErrorMessage;
import in.koala.exception.NonCriticalException;
import in.koala.mapper.DeviceTokenMapper;
import in.koala.service.DeviceTokenService;
import in.koala.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class DeviceTokenServiceImpl implements DeviceTokenService {

    private final DeviceTokenMapper deviceTokenMapper;

    @Override
    public DeviceToken updateToken(String expiredToken, String newToken) {
        DeviceToken deviceToken = this.getDeviceTokenInfoByDeviceToken(expiredToken);

        deviceTokenMapper.updateExTokenToNewToken(expiredToken, newToken);

        deviceToken.setToken(newToken);

        return deviceToken;
    }

    @Override
    public void updateTokenTableUserId(DeviceToken deviceToken) {
        DeviceToken deviceTokenInfo = this.getDeviceTokenInfoByDeviceToken(deviceToken.getToken());

        deviceTokenInfo.setUser_id(deviceToken.getUser_id());

        deviceTokenMapper.updateUserId(deviceTokenInfo);
    }

    @Override
    public DeviceToken getDeviceTokenInfoByDeviceToken(String deviceToken){
        return deviceTokenMapper.getTokenByDeviceToken(deviceToken)
                .orElseThrow(() -> new NonCriticalException(ErrorMessage.DEVICE_TOKEN_NOT_EXIST));
    }

    @Override
    public boolean checkTokenExist(String deviceToken) {
        return deviceTokenMapper.checkTokenExist(deviceToken) > 0;
    }

    @Override
    public void insertDeviceToken(DeviceToken deviceToken) {

        if(this.checkTokenExist(deviceToken.getToken())){
            throw new NonCriticalException(ErrorMessage.DEVICETOKEN_ALREADY_EXIST);
        }

        deviceTokenMapper.insertDeviceToken(deviceToken);
    }

    @Override
    public void updateTokenTableNonUserId(DeviceToken deviceToken) {
        DeviceToken deviceTokenInfo = this.getDeviceTokenInfoByDeviceToken(deviceToken.getToken());

        deviceTokenInfo.setNon_user_id(deviceToken.getNon_user_id());

        deviceTokenMapper.updateNonUserId(deviceTokenInfo);
    }
}
