package in.koala.service;

import in.koala.domain.Notice;

import java.util.List;

public interface HistoryService {

    List<Notice> getEveryNotice(int pageNum);
    void deleteNotice(List<Integer> noticeList);
    void noticeRead(String noticeId);
}
