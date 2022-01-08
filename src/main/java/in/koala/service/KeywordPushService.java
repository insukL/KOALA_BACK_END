package in.koala.service;

import in.koala.domain.Crawling;
import in.koala.domain.Keyword;
import in.koala.enums.CrawlingSite;

import java.util.List;

public interface KeywordPushService {
    void pushKeyword(String deviceToken);
    void pushKeywordAtOnce(String deviceToken) throws Exception;

    void pushKeyword(List<String> keyword, Crawling crawling) throws Exception;
    void subscribe(Keyword keyword, Long id) throws Exception;
    void unsubscribe(Keyword keyword, Long id) throws Exception;
    void modifySubscription(List<CrawlingSite> oldSite, List<CrawlingSite> newSite,
                            Long id, String keywordName) throws Exception;

    void pushKeyword(List<String> keyword, String url, String title, Short site) throws Exception;
}
