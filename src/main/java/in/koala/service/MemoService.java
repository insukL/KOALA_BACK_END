package in.koala.service;

import in.koala.domain.Memo;

import java.util.List;

public interface MemoService {
    void addMemo(Memo memo) throws Exception;
    void updateMemo(Memo memo) throws Exception;
    List<String> getMemo(Long userScrapId) throws Exception;
}
