package in.koala.serviceImpl;

import in.koala.domain.DeviceToken;
import in.koala.enums.ErrorMessage;
import in.koala.exception.NonCriticalException;
import in.koala.mapper.DeviceTokenMapper;
import in.koala.service.DeviceTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DeviceTokenServiceImpl implements DeviceTokenService {

    private final DeviceTokenMapper deviceTokenMapper;

    @Transactional
    @Override
    public DeviceToken updateToken(String expiredToken, String newToken) {
        DeviceToken deviceToken = this.getDeviceTokenInfoByDeviceToken(expiredToken);

        if(this.checkTokenExist(newToken)){
            throw new NonCriticalException(ErrorMessage.DEVICETOKEN_ALREADY_EXIST);
        }

        deviceTokenMapper.updateExTokenToNewToken(expiredToken, newToken);

        deviceToken.setToken(newToken);

        return deviceToken;
    }

    @Transactional
    @Override
    public void updateTokenTableUserId(DeviceToken deviceToken) {
        DeviceToken deviceTokenInfo = this.getDeviceTokenInfoByDeviceToken(deviceToken.getToken());

        deviceTokenInfo.setUserId(deviceToken.getUserId());

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

    @Transactional
    @Override
    public void insertDeviceToken(DeviceToken deviceToken) {

        if(this.checkTokenExist(deviceToken.getToken())){
            throw new NonCriticalException(ErrorMessage.DEVICETOKEN_ALREADY_EXIST);
        }

        deviceTokenMapper.insertDeviceToken(deviceToken);
    }

    @Transactional
    @Override
    public void updateTokenTableNonUserId(DeviceToken deviceToken) {
        DeviceToken deviceTokenInfo = this.getDeviceTokenInfoByDeviceToken(deviceToken.getToken());

        deviceTokenInfo.setNonUserId(deviceToken.getNonUserId());

        deviceTokenMapper.updateNonUserId(deviceTokenInfo);
    }

    @Override
    public List<DeviceToken> getDeviceTokenListByUserId(Long userId) {
        return deviceTokenMapper.getDeviceTokenByUserId(userId);
    }
}
