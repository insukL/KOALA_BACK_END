package in.koala.serviceImpl;

import in.koala.domain.Keyword;
import in.koala.domain.User;
import in.koala.enums.ErrorMessage;
import in.koala.exception.KeywordException;
import in.koala.mapper.KeywordMapper;
import in.koala.service.KeywordService;
import in.koala.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class KeywordServiceImpl implements KeywordService {

    private final KeywordMapper keywordMapper;
    private final UserService userService;

    @Override
    public List<Keyword> myKeywordList() {
        Long userId = userService.getLoginUserInfo().getId();
        return keywordMapper.myKeywordList(userId);
    }

    @Override
    public void registerKeyword(Keyword keyword) {

        Long userId = userService.getLoginUserInfo().getId();
        Timestamp createdAt = new Timestamp(System.currentTimeMillis());

        keyword.setUserId(userId);
        keyword.setCreatedAt(createdAt);

        if(keywordMapper.checkDuplicateUsersKeyword(keyword) != null){
            throw new KeywordException(ErrorMessage.DUPLICATED_KEYWORD_EXCEPTION);
        }
        else{
            keywordMapper.insertUsersKeyword(keyword);
            Map<String, Object> map = new HashMap<>();
            map.put("keywordId", keyword.getId());
            map.put("siteList", keyword.getSiteList());
            map.put("createdAt", keyword.getCreatedAt());
            keywordMapper.insertUsersKeywordSite(map);
        }
    }

    @Override
    public void deleteKeyword(String keywordName) {
        Long userId = userService.getLoginUserInfo().getId();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userId", userId);
        map.put("name", keywordName);
        keywordMapper.deleteKeyword(map);
    }

    @Override
    public void modifyKeyword(String keywordName, Keyword keyword) {
        Long userId = userService.getLoginUserInfo().getId();
        Timestamp updatedAt = new Timestamp(System.currentTimeMillis());
        keyword.setUpdatedAt(updatedAt);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("user_id", userId);
        map.put("keyword_name", keywordName);
        map.put("modifiedKeyword", keyword);

        keywordMapper.modifyKeyword(map);
    }
}
