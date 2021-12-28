package in.koala.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Notice{

    private Long id;
    private String site;
    private String title;
    private String url;
    private String createdAt;

    public Notice(Long id, String site, String title, String url, String createdAt){
        this.id = id;
        this.site = site;
        this.title = title;
        this.url = url;
        this.createdAt = createdAt;
    }

}
