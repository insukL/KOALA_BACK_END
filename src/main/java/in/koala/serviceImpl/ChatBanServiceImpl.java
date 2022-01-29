package in.koala.serviceImpl;

import in.koala.domain.BanWord;
import in.koala.enums.ErrorMessage;
import in.koala.exception.NonCriticalException;
import in.koala.mapper.ChatBanMapper;
import in.koala.service.ChatBanService;
import in.koala.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ChatBanServiceImpl implements ChatBanService {
    @Resource
    private ChatBanMapper chatBanMapper;

    @Resource
    private UserService userService;

    @Override
    public List<BanWord> getBanWordList() throws Exception {
        Long userId = userService.getLoginUserInfo().getId();
        return chatBanMapper.getBanWordList(userId);
    }

    @Override
    public void addBanWord(BanWord banWord) throws Exception {
        Long userId = userService.getLoginUserInfo().getId();
        chatBanMapper.addBanWord(userId, banWord.getWord());
    }

    @Override
    public void updateBanWord(BanWord banWord) throws Exception {
        Long userId = userService.getLoginUserInfo().getId();

        if(!chatBanMapper.checkExistBanWord(banWord.getId(), userId)) {
            throw new NonCriticalException(ErrorMessage.BAN_WORD_NOT_EXIST);
        }
        chatBanMapper.updateBanWord(banWord.getId(), banWord.getWord());
    }

    @Override
    public void deleteBanWord(Long id) throws Exception {
        Long userId = userService.getLoginUserInfo().getId();

        if(!chatBanMapper.checkExistBanWord(id, userId)) {
            throw new NonCriticalException(ErrorMessage.BAN_WORD_NOT_EXIST);
        }
        chatBanMapper.deleteBanWordById(id);
    }
}
