package in.koala.serviceImpl;

import in.koala.domain.DeviceToken;
import in.koala.mapper.DeviceTokenTestMapper;
import in.koala.service.DeviceTokenTestService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;

@Service
@Transactional
public class DeviceTokenTestServiceImpl implements DeviceTokenTestService {
    @Resource
    DeviceTokenTestMapper tokenTestMapper;

    public void insertDeviceToken(DeviceToken deviceToken, Long id){
        tokenTestMapper.insertDeviceToken(deviceToken);
        tokenTestMapper.insertUserDevice(id, deviceToken.getId());
    }
}
