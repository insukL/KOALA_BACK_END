package in.koala.mapper;

import in.koala.domain.Keyword;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface KeywordMapper {
    List<Keyword> myKeywordList(Long userId);
    int insertKeyword(Keyword keyword);
    int insertUsersKeyword(Map map);
    int deleteKeyword(Map map);
    int modifyKeyword(Map map);
}
