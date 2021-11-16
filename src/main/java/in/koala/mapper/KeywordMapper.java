package in.koala.mapper;

import in.koala.domain.Keyword;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface KeywordMapper {
    int insertKeyword(Keyword keyword);
    int insertUsersKeyword(Map map);
}
