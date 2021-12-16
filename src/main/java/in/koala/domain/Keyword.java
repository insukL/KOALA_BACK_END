package in.koala.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class Keyword {

    private Long id;
    private String keyword;
    private short site;
    private short isImportant;

    public Keyword(String keyword, short site){
        this.keyword = keyword;
        this.site = site;
    }

    public Keyword(Long id, String keyword, short site, short isImportant){
        this.id = id;
        this.keyword = keyword;
        this.site = site;
    }

}
