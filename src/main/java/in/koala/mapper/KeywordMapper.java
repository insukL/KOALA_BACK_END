package in.koala.mapper;

import in.koala.domain.Keyword;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface KeywordMapper {

    Long checkDuplicateKeyword(Keyword keyword);
    Long insertKeyword(Keyword keyword);

    Long checkDuplicateUsersKeyword(Keyword keyword);
    int insertUsersKeyword(Keyword keyword);

    List<Keyword> myKeywordList(Long userId);

    int deleteKeyword(Map map);
    int modifyKeyword(Map map);
}
