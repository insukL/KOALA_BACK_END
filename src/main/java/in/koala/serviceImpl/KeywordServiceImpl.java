package in.koala.serviceImpl;

import in.koala.domain.Keyword;
import in.koala.domain.User;
import in.koala.mapper.KeywordMapper;
import in.koala.service.KeywordService;
import in.koala.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeywordServiceImpl implements KeywordService {

    private final KeywordMapper keywordMapper;
    private final UserService userService;

    @Override
    public void registerKeyword(String keyword, short site, boolean isImportant) {
        Keyword tmp = new Keyword(keyword, site);
        keywordMapper.insertKeyword(tmp);
        System.out.println("방금 들어간 데이터 id(pk) : " + tmp.getId());
        User user = userService.getLoginUserInfo();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("user_id", user.getId());
        map.put("keyword_id", tmp.getId());
        map.put("is_important", isImportant);
        if(1 == keywordMapper.insertUsersKeyword(map))
            System.out.println("성공");
        else
            System.out.println("실패");
    }
}
