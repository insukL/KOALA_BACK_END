package in.koala.mapper;

import in.koala.domain.Crawling;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CrawlingMapper {
    String test();
    //void addCrawlingData(Crawling crawling);
    void addCrawlingData(List<Crawling> crawlingList);
}
