package in.koala.service.sns;

import in.koala.domain.sns.SnsUser;

import java.util.Map;

public interface SnsLoginTest {
    String getRedirectUri();
    SnsUser requestUserProfile(String code) throws Exception;
}
