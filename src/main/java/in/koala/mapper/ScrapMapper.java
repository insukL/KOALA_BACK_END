package in.koala.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ScrapMapper {
    void scrapBoard(Long user_id, Long board_id);
    void deleteScrap(Long board_id);
    void deleteAllScrap(Long user_id);
}
