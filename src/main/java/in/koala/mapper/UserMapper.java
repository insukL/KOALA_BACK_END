package in.koala.mapper;

import in.koala.domain.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {
    String test();
    void signUp(User user);
    void snsSignUp(User user);
    Long getIdByAccount(String email);
    User getUserByAccount(String account);
    User getUserById(Long id);
    Integer checkNickname(String nickname);
    User getUserPassword(String account);
    void updateNickname(User user);
}
