package in.koala.service;

import in.koala.domain.Notice;

import java.util.List;

public interface HistoryService {

    List<Notice> getEveryNotice(int pageNum, String sortType);
    Boolean deleteNotice(List<Long> noticeList);
    Boolean deleteNoticeUndo(List<Long> noticeList);
    Boolean noticeRead(String noticeId);
}
