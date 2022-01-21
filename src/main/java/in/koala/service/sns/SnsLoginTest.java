package in.koala.service.sns;

import java.util.Map;

public interface SnsLoginTest extends SnsLogin{
    String getRedirectUri();
    Map requestUserProfile(String code) throws Exception;
}
