package in.koala.service;

import in.koala.domain.User;
import in.koala.enums.SnsType;

import java.io.IOException;
import java.util.Map;

public interface UserService {
    String test();
    Map<String, String> snsLogin(String code, SnsType snsType) throws Exception;
    Map<String, String> login(User user);
    User signUp(User user);
    void requestSnsLogin(SnsType snsType) throws Exception;
    User getMyInfo();
}
