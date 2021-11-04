package in.koala.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class Crawling {
    private Long id;
    private String title;
    private String url;
    private short site;
    private Short isDeleted;
    private String createdAt;
    private String updatedAt;

    public Crawling(String title, String url, Short site, String createdAt){
        this.title = title;
        this.url = url;
        this.site = site;
        this.createdAt = createdAt;
    }
}