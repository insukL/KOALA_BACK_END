package in.koala.mapper;

import in.koala.domain.BanWord;
import in.koala.domain.CrawlingToken;

import java.util.List;

public interface ChatBanMapper {
    List<BanWord> getBanWordList(Long userId);
    void addBanWord(Long userId, String word); // userId, word
    void updateBanWord(Long id, String word); // id, word
    void deleteBanWordById(Long id);

    Boolean checkExistBanWord(Long id, Long userId); // userId, id
    Integer countBanWord(Long userId); // userId
}
