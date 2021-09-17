package in.koala.service;

import in.koala.domain.naverLogin.NaverCallBack;

import java.util.Map;

public interface UserService {
    public String test();
    public Map<String, String> naverLogin(NaverCallBack callBack);
}
