package in.koala.service;

import in.koala.domain.Memo;

public interface MemoService {
    void addMemo(Memo memo) throws Exception;
    void updateMemo(Memo memo) throws Exception;
    String getMemo(Long userScrapId) throws Exception;
}
