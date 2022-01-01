package in.koala.mapper;

import in.koala.domain.DeviceToken;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceTokenTestMapper {
    Long insertDeviceToken(DeviceToken token);
    void insertUserDevice(Long uid, Long did);
    List<String> getDeviceTokenById(Long id);
}
