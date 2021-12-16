package in.koala.serviceImpl;

import in.koala.domain.Keyword;
import in.koala.domain.User;
import in.koala.mapper.KeywordMapper;
import in.koala.service.KeywordService;
import in.koala.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeywordServiceImpl implements KeywordService {

    private final KeywordMapper keywordMapper;
    private final UserService userService;

    @Override
    public List<Keyword> myKeywordList() {
        Long userId = userService.getLoginUserInfo().getId();
        return keywordMapper.myKeywordList(userId);
    }

    @Override
    public void registerKeyword(String keyword, short site, boolean isImportant) {
        Keyword tmp = new Keyword(keyword, site);
        keywordMapper.insertKeyword(tmp);
        System.out.println("방금 들어간 데이터 id(pk) : " + tmp.getId());
        Long userId = userService.getLoginUserInfo().getId();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("user_id", userId);
        map.put("keyword_id", tmp.getId());
        map.put("is_important", isImportant);
        if(1 == keywordMapper.insertUsersKeyword(map))
            System.out.println("성공");
        else
            System.out.println("실패");
    }

    @Override
    public void deleteKeyword(String keywordId) {
        Long userId = userService.getLoginUserInfo().getId();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("user_id", userId);
        map.put("keyword_id", keywordId);
        keywordMapper.deleteKeyword(map);
    }

    @Override
    public void modifyKeyword(String keywordId, String keywordName) {
        Long userId = userService.getLoginUserInfo().getId();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("keyword_id", keywordId);
        map.put("keyword_name", keywordName);
        System.out.println(map);
        keywordMapper.deleteKeyword(map);
    }
}
