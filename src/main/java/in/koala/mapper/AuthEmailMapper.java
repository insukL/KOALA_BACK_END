package in.koala.mapper;

import in.koala.domain.AuthEmail;
import in.koala.enums.EmailType;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

@Repository
public interface AuthEmailMapper {
    // 요청한 당일 전송한 메일의 개수를 반환
    int getAuthEmailNumByUserIdAndType(Long userId, EmailType type, Timestamp start);
    void expirePastAuthEmail(AuthEmail authEmail);
    void insertAuthEmail(AuthEmail authEmail);
    List<AuthEmail> getUndeletedAuthEmailByUserIdAndType(AuthEmail authEmail);
    void setIsAuth(Long id);
    int getUndeletedIsAuthNumByUserId(Long userId, EmailType type);
}
