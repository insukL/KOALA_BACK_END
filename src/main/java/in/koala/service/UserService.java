package in.koala.service;

import in.koala.domain.User;

import java.io.IOException;
import java.util.Map;

public interface UserService {
    String test();
    Map<String, String> snsLogin(String code, String snsType) throws Exception;
    Map<String, String> login(User user);
    User signUp(User user);
    void requestSnsLogin(String snsType) throws Exception;
    User getMyInfo();
}
