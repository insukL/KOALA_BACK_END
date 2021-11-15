package in.koala.mapper;

import in.koala.domain.Crawling;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CrawlingMapper {
    String test();
    int checkDuplicatedData(Crawling crawling);
    void addCrawlingData(List<Crawling> crawlingList);
}
