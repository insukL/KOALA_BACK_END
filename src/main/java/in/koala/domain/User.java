package in.koala.domain;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

public class User {
    private Long id;
    private String account;
    private String password;
    private String find_email;
    private String sns_email;
    private String nickname;
    private String profile;
    private Long user_type;
    private Short is_auth;
    private Timestamp created_at;
    private Timestamp updated_at;


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

    /*
    public static class UserBuilder{
        private Long id;
        private String account;
        private String password;
        private String find_email;
        private String sns_email;
        private String nickname;
        private String profile;
        private Long user_type;
        private Short is_auth;
        private Timestamp created_at;
        private Timestamp updated_at;

        public UserBuilder setId(Long id) {
            this.id = id;
            return this;
        }

        public UserBuilder setAccount(String account) {
            this.account = account;
            return this;
        }

        public UserBuilder setPassword(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder setFind_email(String find_email) {
            this.find_email = find_email;
            return this;
        }

        public UserBuilder setSns_email(String sns_email) {
            this.sns_email = sns_email;
            return this;
        }

        public UserBuilder setNickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        public UserBuilder setProfile(String profile) {
            this.profile = profile;
            return this;
        }

        public UserBuilder setUser_type(Long user_type) {
            this.user_type = user_type;
            return this;
        }

        public UserBuilder setIs_auth(Short is_auth) {
            this.is_auth = is_auth;
            return this;
        }

        public UserBuilder setCreated_at(Timestamp created_at) {
            this.created_at = created_at;
            return this;
        }

        public UserBuilder setUpdated_at(Timestamp updated_at) {
            this.updated_at = updated_at;
            return this;
        }

        public User build(){
            User user = new User()
        }
    }*/

}
