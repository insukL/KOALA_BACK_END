package in.koala.service.sns;

import in.koala.enums.SnsType;

import java.util.Map;

public interface SnsLogin {
    Map requestUserProfile(String code) throws Exception;
    Map requestUserProfileByAccessToken(String accessToken);
    String getRedirectUri();
    SnsType getSnsType();
    String requestAccessToken(String code);
}
