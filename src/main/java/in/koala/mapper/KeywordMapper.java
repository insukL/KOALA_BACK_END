package in.koala.mapper;

import in.koala.domain.Keyword;
import in.koala.domain.Notice;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public interface KeywordMapper {


    Long checkDuplicateUsersKeyword(Keyword keyword);
    int insertUsersKeyword(Keyword keyword);
    int insertUsersKeywordSite(Map map);

    List<Keyword> myKeywordList(Long userId);
    List<Notice> getKeywordNotice(@Param("site") String site,
                                  @Param("map") Map map);

    int deleteKeyword(Map map);

    Set getKeywordSite(Map map);
    Long getKeywordId(Map map);
    int modifyKeywordSite(Map map);
    int modifyKeyword(Map map);
}
