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
    private Short site;
    private String createdAt;
    private Timestamp crawlingAt;
    private Timestamp updatedAt;
    private Short isDeleted;

    public Crawling(String title, String url, Short site, String createdAt, Timestamp crawlingAt){
        this.title = title;
        this.url = url;
        this.site = site;
        this.createdAt = createdAt;
        this.crawlingAt = crawlingAt;
    }
}