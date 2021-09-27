package in.koala.domain;

public class Scrap {
    private Long id;
    private Long user_id;
    private Long scrap_id;
    private Long is_deleted;
    private String created_at;
    private String updated_at;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Long getScrap_id() {
        return scrap_id;
    }

    public void setScrap_id(Long scrap_id) {
        this.scrap_id = scrap_id;
    }

    public Long getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(Long is_deleted) {
        this.is_deleted = is_deleted;
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
