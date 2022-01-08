package in.koala.mapper;

import in.koala.domain.Notice;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryMapper {

    List<Notice> getEveryNotice(Long userId, int pageNum);
    void deleteNotice(List<Integer> noticeList);
    Integer noticeRead(String noticeId);
}
