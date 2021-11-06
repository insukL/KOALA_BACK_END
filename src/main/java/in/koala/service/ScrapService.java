package in.koala.service;


import in.koala.domain.Board;
import in.koala.domain.Scrap;

import java.util.List;

public interface ScrapService {
    void Scrap(Scrap scrap) throws Exception;
    List<Board> getScrap() throws Exception;
    void deleteScrap(Long boardId) throws Exception;
    void deleteAllScrap(Long userId) throws Exception;
}
