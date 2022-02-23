package in.koala.domain.sns;

import in.koala.domain.user.NormalUser;
import in.koala.enums.SnsType;
import in.koala.enums.UserType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SnsUser {
    private String account;
    private String email;
    private String nickname;
    private String profile;
    private SnsType snsType;

    @Builder
    public SnsUser(String account, String email, String nickname, String profile, SnsType snsType) {
        this.account = account;
        this.email = email;
        this.nickname = nickname;
        this.profile = profile;
        this.snsType = snsType;
    }

    public NormalUser toNormalUser(){
        return NormalUser.builder()
                .account(account)
                .snsEmail(email)
                .profile(profile)
                .nickname(nickname)
                .snsType(snsType)
                .userType(UserType.NORMAL)
                .build();
    }
}
