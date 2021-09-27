package in.koala.mapper;

import in.koala.domain.Memo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemoMapper {
    void addMemo(Memo memo);
    void deleteMemo(Memo memo);
    void updateMemo(Memo memo);
    String getMemo(Long user_scrap_id);
}
