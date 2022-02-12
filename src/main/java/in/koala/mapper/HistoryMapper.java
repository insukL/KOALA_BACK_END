package in.koala.mapper;

import in.koala.domain.Notice;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryMapper {

    List<Notice> getEveryNotice(Long userId, int pageNum, String sortType);
    Integer deleteNotice(List<Long> noticeList);
    Integer deleteNoticeUndo(List<Long> noticeList);
    Integer noticeRead(String noticeId);
}
