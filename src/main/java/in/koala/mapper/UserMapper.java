package in.koala.mapper;

import in.koala.domain.user.NonUser;
import in.koala.domain.user.NormalUser;
import in.koala.domain.user.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {
    String test();
    void signUp(NormalUser user);
    void snsSignUp(NormalUser user);
    Long getIdByAccount(String account);
    NormalUser getUserByAccount(String account);
    NormalUser getNormalUserById(Long id);
    NonUser getNonUserById(Long id);
    NormalUser getUserByFindEmail(String email);
    Integer checkNickname(String nickname);
    NormalUser getUserPassword(String account);
    void updateNickname(NormalUser user);
    void updateIsAuth(Long id);
    void updatePassword(NormalUser user);
    void softDeleteUser(User user);
    void softDeleteNormalUser(NormalUser user);
    Long insertNonMemberUser(NonUser user);
    void updateUserProfile(String url, Long id);
    void insertUser(User user);
}
