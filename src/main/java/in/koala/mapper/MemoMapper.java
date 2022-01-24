package in.koala.mapper;

import in.koala.domain.Memo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemoMapper {
    void addMemo(Memo memo);
    void updateMemo(Memo memo);
    List<String> getMemo(Long userScrapId);
    Boolean checkMemoExist(Long userScrapId);
}
