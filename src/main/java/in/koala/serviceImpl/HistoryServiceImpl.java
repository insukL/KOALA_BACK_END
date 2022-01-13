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
    public List<Notice> getEveryNotice(int pageNum) throws Exception{

        Long userId = userService.getLoginUserInfo().getId();

        if(pageNum <= 1) pageNum = 0;
        else {
            pageNum = (pageNum * 10) + 1;
        }

        List<Notice> result = historyMapper.getEveryNotice(userId, pageNum);

        if(result.isEmpty()){
            throw new HistoryException(ErrorMessage.NOTICE_NOT_EXIST);
        }
        else
            return result;
    }

    @Override
    public void deleteNotice(List<Integer> noticeList) {

        if(noticeList.isEmpty())
            throw new HistoryException(ErrorMessage.NOTICE_NOT_SELECTED);

        historyMapper.deleteNotice(noticeList);
    }

    @Override
    public void noticeRead(String noticeId) {
        historyMapper.noticeRead(noticeId);
    }
}
