package in.koala.serviceImpl;

import in.koala.domain.Crawling;
import in.koala.domain.Keyword;
import in.koala.domain.Notice;
import in.koala.domain.User;
import in.koala.enums.CrawlingSite;
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
    public List<Integer> convertSiteList(List<CrawlingSite> siteList) {

        List<Integer> convertSiteList = new ArrayList<>();

        for(CrawlingSite site : siteList){
            for(CrawlingSite value : CrawlingSite.values()){
                if(value.equals(site)){
                    convertSiteList.add(value.ordinal());
                }
            }
        }

        return convertSiteList;
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
            map.put("siteList", convertSiteList(keyword.getSiteList()));
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

        Set<Integer> addingList = new HashSet<>(convertSiteList(keyword.getSiteList()));
        Set<Integer> addingListCopy = new HashSet<>();
        addingListCopy.addAll(addingList);
        Set<Integer> existingList = new HashSet<>(keywordMapper.getKeywordSite(map));

        addingList.removeAll(existingList);
        existingList.removeAll(addingListCopy);

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

    @Override
    public List<Notice> getKeywordNotice(String keywordName, String site) {

        Long userId = userService.getLoginUserInfo().getId();
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("keywordName", keywordName);
        return keywordMapper.getKeywordNotice(site, map);
    }
}
