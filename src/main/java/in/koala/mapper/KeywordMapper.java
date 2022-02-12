package in.koala.mapper;

import in.koala.domain.Keyword;
import in.koala.domain.Notice;
import in.koala.enums.CrawlingSite;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public interface KeywordMapper {


    Long checkDuplicateUsersKeyword(Keyword keyword);
    int insertUsersKeyword(Keyword keyword);
    int insertUsersKeywordSite(Long keywordId, List<CrawlingSite> siteList);

    List<Keyword> myKeywordList(Long userId);
    List<Notice> getKeywordNotice(String keywordName, String site, Long userId);
    List<Notice> getSearchNotice(String keywordName, String site, String word, Long userId);
    List<String> searchKeyword(String keyword);

    List<CrawlingSite> getSiteList(Long userId, String keywordName);
    List<String> recommendKeyword();
    List<CrawlingSite> recommendSite();
    Integer countKeywordNum(Long userId);

    Integer noticeRead(String noticeId);
    Integer deleteNotice(List<Long> noticeList);
    Integer deleteNoticeUndo(List<Long> noticeList);

    void deleteKeyword(Long userId, String keywordName);

    Set<CrawlingSite> getKeywordSite(Long userId, String keywordName);
    Long getKeywordId(Long userId, String keywordName);
    int modifyKeywordSite(Set<CrawlingSite> existingList, Long keywordId);
    int modifyKeyword(Long userId, String keywordName, Keyword keyword);
}
