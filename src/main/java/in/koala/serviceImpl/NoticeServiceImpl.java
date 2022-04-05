package in.koala.serviceImpl;

import in.koala.domain.PushNotice;
import in.koala.mapper.NoticeMapper;
import in.koala.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeMapper noticeMapper;

    @Override
    public void insertNotice(List<PushNotice> pushNoticeList) {
        noticeMapper.insertNotice(pushNoticeList);
    }
}
