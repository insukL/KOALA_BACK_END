package in.koala.service;

import in.koala.domain.Crawling;
import in.koala.domain.Keyword;
import in.koala.enums.CrawlingSite;

import java.util.List;

public interface KeywordPushService {
    void subscribe(Keyword keyword, Long id) throws Exception;
    void unsubscribe(Keyword keyword, Long id) throws Exception;
    void modifySubscription(List<CrawlingSite> oldSite, List<CrawlingSite> newSite,
                            Long id, String keywordName) throws Exception;
    void pushNotification(List<String> tokens, String keyword, CrawlingSite site, String url) throws Exception;
}
