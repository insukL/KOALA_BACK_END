package in.koala.service;


import in.koala.domain.Crawling;
import in.koala.domain.Scrap;

import java.util.List;

public interface ScrapService {
    void Scrap(Scrap scrap) throws Exception;
    List<Crawling> getScrap() throws Exception;
    void deleteScrap(Long boardId) throws Exception;
}
