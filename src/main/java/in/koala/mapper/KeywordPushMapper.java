package in.koala.mapper;

import in.koala.domain.PushNotice;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Repository
public interface KeywordPushMapper {
    List<PushNotice> pushKeywordByLatelyCrawlingTime(Timestamp mostRecentCrawlingTime);
    List<Map<String, String>> pushKeyword(Long userId);
}
