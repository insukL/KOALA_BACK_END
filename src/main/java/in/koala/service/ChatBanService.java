package in.koala.service;

import in.koala.domain.BanWord;

import java.util.List;

public interface ChatBanService {
    List<BanWord> getBanWordList() throws Exception;
    void addBanWord(BanWord banWord) throws Exception;
    void updateBanWord(BanWord banWord) throws Exception;
    void deleteBanWord(Long id) throws Exception;
}
