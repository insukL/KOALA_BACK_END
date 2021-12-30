package in.koala.service;

import in.koala.domain.Crawling;
import in.koala.domain.Keyword;

import java.util.List;

public interface KeywordPushService {
    void pushKeyword(String deviceToken);
    void pushKeywordAtOnce(String deviceToken) throws Exception;
  
    void pushKeyword(List<String> keyword, Crawling crawling) throws Exception;
    void subscribe(Keyword keyword, String deviceToken) throws Exception;
    void unsubscribe(Keyword keyword, String deviceToken) throws Exception;
}
