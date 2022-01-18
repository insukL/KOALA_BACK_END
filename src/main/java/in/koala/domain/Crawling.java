package in.koala.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import in.koala.enums.CrawlingSite;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Crawling {

    private Long id;
    private String title;
    private String url;
    private CrawlingSite site;
    private String createdAt;
    private Timestamp crawlingAt;
    private Timestamp updatedAt;
    private Short isDeleted;

    public Crawling(String title, String url, CrawlingSite site, String createdAt, Timestamp crawlingAt){
        this.title = title;
        this.url = url;
        this.site = site;
        this.createdAt = createdAt;
        this.crawlingAt = crawlingAt;
    }

    public Crawling(Long id, String title, String url, CrawlingSite site, String createdAt, Timestamp crawlingAt){
        this.id = id;
        this.title = title;
        this.url = url;
        this.site = site;
        this.createdAt = createdAt;
        this.crawlingAt = crawlingAt;
    }

}