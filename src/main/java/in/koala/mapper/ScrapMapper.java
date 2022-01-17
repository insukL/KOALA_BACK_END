package in.koala.mapper;

import in.koala.domain.Crawling;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScrapMapper {
    List<Crawling> getScrapList(Long userId);
    void scrapBoard(Long userId, Long crawlingId);
    void deleteScrap(List<Long> crawlingId);
    Boolean checkBoardExist(Long crawlingId);
    Boolean checkScrapExist(Long crawlingId);
    Long checkAlreadyScraped(Long userId, Long crawlingId);
    Boolean checkScrapExistByMemo(Long userId, Long userScrapId);
}
