package in.koala.mapper;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface KeywordPushMapper {
    List<Map<String, String>> pushKeyword(Long userId);
}
