package in.koala.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Repository
public interface KeywordPushMapper {
    List<Map<String, String>> pushKeywordByLatelyCrawlingTime(Timestamp latelyCrawlingTime);
    List<Map<String, String>> pushKeyword(Long userId);
}
