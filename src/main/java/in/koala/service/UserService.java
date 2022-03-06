package in.koala.service;

import in.koala.domain.AuthEmail;
import in.koala.domain.JWToken;
import in.koala.domain.user.NormalUser;
import in.koala.domain.user.User;
import in.koala.enums.EmailType;
import in.koala.enums.SnsType;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface UserService {
    JWToken snsLogin(String code, SnsType snsType) throws Exception;
    JWToken snsSingIn(SnsType snsType, String deviceToken);
    JWToken login(NormalUser user, String deviceToken);
    NormalUser signUp(NormalUser user);
    void requestSnsLogin(SnsType snsType) throws Exception;
    User getLoginUserInfo();
    NormalUser getLoginNormalUserInfo();
    void updateNickname(String nickname);
    Boolean checkNickname(String nickname);
    Boolean checkAccount(String account);
    JWToken refresh();
    void sendEmail(AuthEmail authEmail, EmailType emailType);
    void certificateEmail(AuthEmail authEmail, EmailType emailType);
    boolean isUniversityCertification();
    void changePassword(NormalUser user);
    String findAccount(String email);
    void softDeleteUser();
    Boolean checkFindEmail(String email);
    String editProfile(MultipartFile multipartFile);
    JWToken getSocketToken();
    JWToken nonMemberLogin(String deviceToken);
}
