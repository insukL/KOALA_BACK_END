package in.koala.service;

import in.koala.domain.Crawling;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

public interface CrawlingService {

    String test();
    void updateLog(Short site, Timestamp crawlingAt);
    void updateTable(List<Crawling> crawlingInsertList, List<Crawling> crawlingUpdateList);
    Timestamp getLatelyCrawlingTime();

    Boolean dormCrawling(Timestamp crawlingAt) throws Exception;
    Boolean portalCrawling(Timestamp crawlingAt) throws Exception;
    Boolean youtubeCrawling(Timestamp crawlingAt) throws Exception;

    Boolean executeAll() throws Exception;
}
