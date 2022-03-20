package in.koala.mapper;

import in.koala.domain.Crawling;
import in.koala.domain.CrawlingToken;
import in.koala.enums.CrawlingSite;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Repository
public interface CrawlingMapper {

    String test();

    Boolean checkDuplicatedData(Crawling crawling);
    Timestamp getMostRecentCrawlingTime();

    int updateLog(CrawlingSite site, Timestamp crawlingAt);
    void addCrawlingData(List<Crawling> crawlingInsertList);
    void updateCrawlingData(List<Crawling> crawlingUpdateList);

    void addToken(CrawlingToken token);
    List<CrawlingToken> getToken();
    void updateToken(CrawlingToken token);
    void deleteTokenById(Long id);
    Boolean checkTokenById(Long id);
    CrawlingToken getTokenById(Long id);
}
