package in.koala.serviceImpl;

import in.koala.annotation.Auth;
import in.koala.domain.ChatReport;
import in.koala.mapper.ReportMapper;
import in.koala.service.ChatReportService;
import in.koala.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ChatReportServiceImpl implements ChatReportService {
    @Resource
    private ReportMapper reportMapper;

    @Resource
    private UserService userService;

    @Auth
    @Override
    public void reportChat(ChatReport chatReport){
        //TODO : 예외 처리


        chatReport.setUserId(userService.getLoginUserInfo().getId());
        reportMapper.insertReport(chatReport);
    }
}
