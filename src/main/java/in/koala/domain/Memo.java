package in.koala.domain;

import org.hibernate.validator.constraints.Length;

public class Memo {
    private Long user_scrap_id;
    @Length(max=100, message = "메모는 100자까지 작성할 수 있습니다.")
    private String memo;
    private String created_at;
    private String updated_at;

    public Long getUser_scrap_id() {
        return user_scrap_id;
    }

    public void setUser_scrap_id(Long user_scrap_id) {
        this.user_scrap_id = user_scrap_id;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

}
