package in.koala.mapper;

import in.koala.domain.Board;
import in.koala.domain.Scrap;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ScrapMapper {
    List<Board> getScrapList(Long userId);

    void scrapBoard(Long userId, Long boardId);
    void deleteScrap(Long boardId);
    void deleteAllScrap(Long userId);

    Boolean checkBoardExist(Long boardId);
    Long checkAlreadyScraped(Long userId, Long boardId);

}
