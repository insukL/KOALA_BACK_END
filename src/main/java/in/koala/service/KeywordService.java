package in.koala.service;

import in.koala.domain.Keyword;
import in.koala.domain.Notice;
import in.koala.enums.CrawlingSite;

import java.util.List;

public interface KeywordService {

    List<Keyword> myKeywordList();

    List<Notice> getKeywordNotice(String keywordName, String site);
    List<Notice> getSearchNotice(String keywordName, String site, String word);

    Boolean noticeRead(String noticeId);
    void deletedNotice(List<Integer> noticeList);

    List<String> searchKeyword(String keyword);
    List<String> recommendKeyword();

    List<Integer> convertSiteList(List<CrawlingSite> siteList);

    void registerKeyword(Keyword keyword) throws Exception;
    void deleteKeyword(String keywordId) throws Exception;
    void modifyKeyword(String keywordName, Keyword keyword) throws Exception;
}
