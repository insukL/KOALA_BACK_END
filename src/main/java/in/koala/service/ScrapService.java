package in.koala.service;


import in.koala.domain.Scrap;

public interface ScrapService {
    void Scrap(Scrap scrap) throws Exception;
    void deleteScrap(Long board_id) throws Exception;
    void deleteAllScrap(Long user_id) throws Exception;
    //List<Board> getScrap(Long user_id) throws Exception;
}
