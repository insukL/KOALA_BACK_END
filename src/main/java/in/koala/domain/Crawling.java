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
@Builder
public class Crawling {

    private Long id;
    private String title;
    private String url;
    private CrawlingSite site;
    private String createdAt;
    private Timestamp crawlingAt;
    private Timestamp updatedAt;
    private Short isDeleted;

}