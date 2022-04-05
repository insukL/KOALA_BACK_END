package in.koala.service;

import in.koala.domain.PushNotice;

import java.util.List;

public interface NoticeService {

    void insertNotice(List<PushNotice> pushNoticeList);
}
