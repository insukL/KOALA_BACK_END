package in.koala.mapper;

import in.koala.domain.Keyword;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface KeywordMapper {


    Long checkDuplicateUsersKeyword(Keyword keyword);
    int insertUsersKeyword(Keyword keyword);
    void insertUsersKeywordSite(Map map);

    List<Keyword> myKeywordList(Long userId);

    int deleteKeyword(Map map);

    int modifyKeyword(Map map);
}
