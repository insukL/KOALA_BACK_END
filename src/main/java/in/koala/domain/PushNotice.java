package in.koala.domain;

import in.koala.enums.CrawlingSite;
import lombok.Getter;

import java.util.List;

@Getter
public class PushNotice {

    private List<String> tokenList;
    private String keyword;
    private CrawlingSite site;
    private String url;
}
