package in.koala.service;

import in.koala.domain.Keyword;
import in.koala.domain.Notice;
import in.koala.enums.CrawlingSite;

import java.util.List;

public interface KeywordService {

    List<Keyword> myKeywordList();
    Boolean registerKeyword(Keyword keyword) throws Exception;
    Boolean deleteKeyword(String keywordId) throws Exception;
    Boolean modifyKeyword(String keywordName, Keyword keyword) throws Exception;

    List<String> searchKeyword(String keyword);

    List<Notice> getKeywordNotice(String keywordName, String site);
    List<Notice> getSearchNotice(String keywordName, String site, String word);
    Boolean deleteNotice(List<Long> noticeList);
    Boolean deleteNoticeUndo(List<Long> noticeList);
    Boolean noticeRead(String noticeId);

    List<String> recommendKeyword();

    List<String> getSiteList(String keywordName);
    List<String> recommendSite();
    List<String> searchSite(String site);
}
