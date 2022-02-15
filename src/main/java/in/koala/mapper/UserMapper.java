package in.koala.mapper;

import in.koala.domain.user.NonUser;
import in.koala.domain.user.NormalUser;
import in.koala.domain.user.User;
import in.koala.enums.UserType;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserMapper {
    String test();
    void signUp(NormalUser user);
    void snsSignUp(NormalUser user);
    Long getIdByAccount(String account);
    Optional<NormalUser> getUserByAccount(String account);
    Optional<NormalUser> getNormalUserById(Long id);
    Optional<NonUser> getNonUserById(Long id);
    Optional<NormalUser> getUserByFindEmail(String email);
    UserType getUserType(Long id);
    Integer checkNickname(String nickname);
    Optional<NormalUser> getUserPassword(String account);
    void updateNickname(NormalUser user);
    void updateIsAuth(Long id);
    void updatePassword(NormalUser user);
    void softDeleteUser(User user);
    void softDeleteNormalUser(NormalUser user);
    Long insertNonMemberUser(NonUser user);
    void updateUserProfile(String url, Long id);
    void insertUser(User user);
}
