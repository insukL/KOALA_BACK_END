package in.koala.service;

import in.koala.domain.User;
import in.koala.domain.naverLogin.NaverCallBack;

import java.util.Map;

public interface UserService {
    String test();
    Map<String, String> snsLogin(String code, String snsType) throws Exception;
    Map<String, String> login(User user);
    User signUp(User user);
}
