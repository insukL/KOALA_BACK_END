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
import java.util.*;

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

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userId", userId);
        map.put("name", keywordName);

        Set<String> addingList = new HashSet<>(keyword.getSiteList());
        Set<String> addingListCopy = new HashSet<>();
        addingListCopy.addAll(addingList);
        Set<String> existingList = new HashSet<>(keywordMapper.getKeywordSite(map));

        System.out.println("추가한 키워드 사이트 크기 : " + addingList.size());
        System.out.println("카피한 키워드 사이트 크기 : " + addingListCopy.size());
        System.out.println("기존의 키워드 사이트 크기 : " + existingList.size());

        addingList.removeAll(existingList);
        existingList.removeAll(addingListCopy);

        System.out.println("뺀 후 추가할 키워드 사이트 : "); // insert
        for(String site : addingList){
            System.out.println(site);
        }

        System.out.println("기존의 키워드 사이트 : "); // delete
        for(String site : existingList) {
            System.out.println(site);
        }

        Long keywordId = keywordMapper.getKeywordId(map);

        map.put("keywordId", keywordId);
        map.put("createdAt", new Timestamp(System.currentTimeMillis()));

        if(!addingList.isEmpty()) {
            map.put("siteList", addingList);
            if(keywordMapper.insertUsersKeywordSite(map) > 0){
                System.out.println("키워드 추가 성공");
            }
            else {
                System.out.println("키워드 추가 실패");
            }
        }

        if(!existingList.isEmpty()){
            map.put("existingList", existingList);
            if(keywordMapper.modifyKeywordSite(map) > 0){
                System.out.println("키워드 업데이트 성공");
            }
            else {
                System.out.println("키워드 업데이트 실패");
            }
        }

        map.put("modifiedKeyword", keyword);
        keywordMapper.modifyKeyword(map);
    }
}
