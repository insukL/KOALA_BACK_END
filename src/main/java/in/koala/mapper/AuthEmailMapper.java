package in.koala.mapper;

import in.koala.domain.AuthEmail;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.sql.Timestamp;

@Repository
public interface AuthEmailMapper {
    // 요청한 당일 전송한 메일의 개수를 반환
    int getAuthEmailNumByUserIdAndType(Long userId, Short type, Timestamp start, Timestamp end);
    void expirePastAuthEmail(AuthEmail authEmail);
    void insertAuthEmail(AuthEmail authEmail);
}
