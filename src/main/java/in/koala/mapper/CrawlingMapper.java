package in.koala.mapper;

import in.koala.domain.Crawling;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Repository
public interface CrawlingMapper {

    String test();

    int checkDuplicatedData(Crawling crawling);

    int updateLog(@Param("site") Short site,
                  @Param("crawling_at") Timestamp crawlingAt);

    void addCrawlingData(List<Crawling> crawlingInsertList);

    void updateCrawlingData(List<Crawling> crawlingUpdateList);
    Timestamp getLatelyCrawlingTime();
}
