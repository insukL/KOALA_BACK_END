package in.koala.service;

import in.koala.domain.AuthEmail;
import in.koala.domain.User;
import in.koala.enums.SnsType;

import java.util.Map;

public interface UserService {
    String test();
    Map<String, String> snsLogin(String code, SnsType snsType) throws Exception;
    Map<String, String> snsSingIn(SnsType snsType);
    Map<String, String> login(User user);
    User signUp(User user);
    void requestSnsLogin(SnsType snsType) throws Exception;
    User getLoginUserInfo();
    void updateNickname(String nickname);
    Boolean checkNickname(String nickname);
    Boolean checkAccount(String account);
    Map<String, String> refresh();
    void sendEmail(AuthEmail authEmail);
    void certificateEmail(AuthEmail authEmail);
    boolean isEmailCertification();
    void changePassword(User user);
    String findAccount(String email);
    void softDeleteUser();
    Boolean checkFindEmail(String email);
    User createNonMemberUserAndDeviceToken(String token);

}
