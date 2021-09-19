package in.koala.domain;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

public class User {
    Long id;
    String account;
    String password;
    String find_email;
    String sns_email;
    String nickname;
    String profile;
    Long user_type;
    Short is_auth;
    Timestamp created_at;
    Timestamp updated_at;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFind_email() {
        return find_email;
    }

    public void setFind_email(String find_email) {
        this.find_email = find_email;
    }

    public String getSns_email() {
        return sns_email;
    }

    public void setSns_email(String sns_email) {
        this.sns_email = sns_email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public Long getUser_type() {
        return user_type;
    }

    public void setUser_type(Long user_type) {
        this.user_type = user_type;
    }

    public Short getIs_auth() {
        return is_auth;
    }

    public void setIs_auth(Short is_auth) {
        this.is_auth = is_auth;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public Timestamp getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Timestamp updated_at) {
        this.updated_at = updated_at;
    }

}
