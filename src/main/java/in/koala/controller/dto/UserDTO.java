package in.koala.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import in.koala.domain.user.NonUser;
import in.koala.domain.user.NormalUser;
import in.koala.domain.user.User;
import in.koala.enums.SnsType;
import in.koala.enums.UserType;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserDTO {
    private Long id;
    private String findEmail;
    private UserType userType;
    private String account;
    private String profile;
    private Short isAuth;
    private SnsType snsType;
    private String nickname;

    public UserDTO(User user) {
        if(user.getUserType().equals(UserType.NORMAL)){
            NormalUser normalUser = (NormalUser) user;
            this.findEmail = normalUser.getFindEmail();
            this.account = normalUser.getAccount();
            this.profile = normalUser.getProfile();
            this.snsType = normalUser.getSnsType();
            this.nickname = normalUser.getNickname();
        }
        this.id = user.getId();
        this.userType = user.getUserType();
    }

    public UserDTO(NormalUser normalUser){
        this.findEmail = normalUser.getFindEmail();
        this.account = normalUser.getAccount();
        this.profile = normalUser.getProfile();
        this.snsType = normalUser.getSnsType();
        this.nickname = normalUser.getNickname();
        this.id = normalUser.getId();
        this.userType = normalUser.getUserType();
    }
}
