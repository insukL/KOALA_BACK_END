package in.koala.mapper;

import org.springframework.stereotype.Repository;

@Repository
public interface TokenMapper {
    void insertDeviceTokenNonUser(Long nonUserId, String deviceToken);
    void updateUserId(Long userId, String token);
    void updateTokenByUserId(Long userId, String token);
    void updateTokenByNonUserId(Long nonUserId, String token);
}
