package in.koala.service;

import in.koala.domain.User;
import in.koala.domain.naverLogin.NaverCallBack;

import java.util.Map;

public interface UserService {
    String test();
    Map<String, String> naverLogin(NaverCallBack callBack);
    Map<String, String> login(User user);
    Map<String, String> signUp(User user);
}
