package in.koala.mapper;

import in.koala.domain.Crawling;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScrapMapper {
    List<Crawling> getScrapList(Long userId);
    void scrapBoard(Long userId, Long boardId);
    Long getScrapId(Long userId, Long boardId);
    void deleteScrap(List<Long> boardId);
    Boolean checkBoardExist(Long boardId);
    Boolean checkScrapExist(Long boardId);
    Long checkAlreadyScraped(Long userId, Long boardId);

}
