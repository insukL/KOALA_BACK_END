package in.koala.service;

import in.koala.domain.Keyword;
import in.koala.domain.Notice;
import in.koala.enums.CrawlingSite;

import java.util.List;

public interface KeywordService {
    List<Keyword> myKeywordList();
    List<Notice> getKeywordNotice(String keywordName, String site);
    List<Integer> convertSiteList(List<CrawlingSite> siteList);
    void registerKeyword(Keyword keyword);
    void deleteKeyword(String keywordId);
    void modifyKeyword(String keywordName, Keyword keyword);
}
