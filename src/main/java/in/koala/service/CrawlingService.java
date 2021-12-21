package in.koala.service;

import in.koala.domain.Crawling;

import java.sql.Timestamp;
import java.util.List;

public interface CrawlingService {

    String test();
    void updateLog(String site, Timestamp crawlingAt);
    void updateTable(List<Crawling> crawlingInsertList, List<Crawling> crawlingUpdateList);
    void dormCrawling() throws Exception;
    void portalCrawling() throws Exception;
    void youtubeCrawling() throws Exception;

}
