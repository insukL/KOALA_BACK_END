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
    int insertUsersKeywordSite(Long keywordId, Timestamp createdAt, List<Integer> siteList);

    List<Keyword> myKeywordList(Long userId);
    List<Notice> getKeywordNotice(String keywordName, String site, Long userId);

    int deleteKeyword(Long userId, String keywordName);

    Set getKeywordSite(Long userId, String keywordName);
    Long getKeywordId(Long userId, String keywordName);
    int modifyKeywordSite(Set<Integer> existingList, Long keywordId);
    int modifyKeyword(Long userId, String keywordName, Timestamp createdAt, Keyword keyword);
}
