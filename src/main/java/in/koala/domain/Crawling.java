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
    private Short from;
    private Short isDeleted;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    @Builder
    public Crawling(String title, String url){
        this.title = title;
        this.url = url;
    }
}
