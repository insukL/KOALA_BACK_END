package in.koala.mapper;

import in.koala.domain.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {
    String test();
    void signUp(User user);
    void snsSignUp(User user);
    Long getIdByAccount(String account);
    User getUserByAccount(String account);
    User getUserById(Long id);
    User getUserByFindEmail(String email);
    Integer checkNickname(String nickname);
    User getUserPassword(String account);
    void updateNickname(User user);
    void updateIsAuth(Long id);
    void updatePassword(User user);
}
