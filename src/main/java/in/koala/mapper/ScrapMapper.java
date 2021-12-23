package in.koala.mapper;

import in.koala.domain.Crawling;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ScrapMapper {
    List<Crawling> getScrapList(Long userId);
    void scrapBoard(Long userId, Long boardId);
    Long getScrapId(Long userId, Long boardId);
    void deleteScrap(Long boardId);
    Boolean checkBoardExist(Long boardId);
    Long checkAlreadyScraped(Long userId, Long boardId);
}
