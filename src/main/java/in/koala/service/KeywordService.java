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
    Boolean deletedNotice(List<Integer> noticeList);

    List<String> searchKeyword(String keyword);
    List<String> searchSite(String site);

    List<String> recommendKeyword();
    List<String> recommendSite();

    Boolean registerKeyword(Keyword keyword) throws Exception;
    Boolean deleteKeyword(String keywordId) throws Exception;
    Boolean modifyKeyword(String keywordName, Keyword keyword) throws Exception;

    List<String> getSiteList(String keywordName);
}
