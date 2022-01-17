package in.koala.service;

import in.koala.domain.AuthEmail;
import in.koala.domain.user.NormalUser;
import in.koala.domain.user.User;
import in.koala.enums.EmailType;
import in.koala.enums.SnsType;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface UserService {
    String test();
    Map<String, String> snsLogin(String code, SnsType snsType) throws Exception;
    Map<String, String> snsSingIn(SnsType snsType, String deviceToken);
    Map<String, String> login(NormalUser user, String deviceToken);
    User signUp(NormalUser user);
    void requestSnsLogin(SnsType snsType) throws Exception;
    User getLoginUserInfo();
    NormalUser getLoginNormalUserInfo();
    void updateNickname(String nickname);
    Boolean checkNickname(String nickname);
    Boolean checkAccount(String account);
    Map<String, String> refresh();
    void sendEmail(AuthEmail authEmail, EmailType emailType);
    void certificateEmail(AuthEmail authEmail, EmailType emailType);
    boolean isUniversityCertification();
    void changePassword(NormalUser user);
    Map findAccount(String email);
    void softDeleteUser();
    Boolean checkFindEmail(String email);
    Map editProfile(MultipartFile multipartFile);
    Map nonMemberLogin(String deviceToken);
}
