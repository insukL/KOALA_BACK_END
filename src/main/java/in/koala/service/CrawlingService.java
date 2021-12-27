package in.koala.service;

import in.koala.domain.Crawling;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

public interface CrawlingService {

    String test();
    void updateLog(Short site, Timestamp crawlingAt);
    void updateTable(List<Crawling> crawlingInsertList, List<Crawling> crawlingUpdateList);

    void dormCrawling(Timestamp crawlingAt) throws Exception;
    void portalCrawling(Timestamp crawlingAt) throws Exception;
    void youtubeCrawling(Timestamp crawlingAt) throws Exception;

    void executeAll() throws Exception;
    Timestamp getLatelyCrawlingTime();

}
