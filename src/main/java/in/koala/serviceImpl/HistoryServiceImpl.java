package in.koala.serviceImpl;

import in.koala.domain.Notice;
import in.koala.enums.ErrorMessage;
import in.koala.exception.HistoryException;
import in.koala.mapper.HistoryMapper;
import in.koala.service.HistoryService;
import in.koala.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class HistoryServiceImpl implements HistoryService {

    private final HistoryMapper historyMapper;
    private final UserService userService;

    @Override
    public List<Notice> getEveryNotice(int pageNum, String sortType){

        Long userId = userService.getLoginUserInfo().getId();

        if(pageNum <= 1) pageNum = 0;
        else {
            pageNum = 5 * (pageNum-1);
        }

        return historyMapper.getEveryNotice(userId, pageNum, sortType);
    }

    @Override
    public Boolean deleteNotice(List<Integer> noticeList) {

        if(noticeList.isEmpty())
            throw new HistoryException(ErrorMessage.NOTICE_NOT_SELECTED);

        if(historyMapper.deleteNotice(noticeList)==1)
            return true;
        else
            return false;
    }

    @Override
    public Boolean deleteNoticeUndo(List<Integer> noticeList) {
        if(noticeList.isEmpty())
            throw new HistoryException(ErrorMessage.NOTICE_NOT_SELECTED);
        if(historyMapper.deleteNoticeUndo(noticeList)==1)
            return true;
        else
            return false;
    }

    @Override
    public Boolean noticeRead(String noticeId) {
        if(historyMapper.noticeRead(noticeId)==1)
            return true;
        else
            return false;
    }
}
