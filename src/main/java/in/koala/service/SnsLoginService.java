package in.koala.service;

import in.koala.domain.User;
import org.springframework.http.HttpEntity;

public interface SnsLoginService {
    String requestAccessToken(String code);
    User requestUserProfile(String code) throws Exception;
    String getRedirectUri();
}
