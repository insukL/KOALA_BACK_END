package in.koala.serviceImpl;

import in.koala.domain.Keyword;
import in.koala.domain.Notice;
import in.koala.enums.CrawlingSite;
import in.koala.enums.ErrorMessage;
import in.koala.exception.KeywordException;
import in.koala.mapper.KeywordMapper;
import in.koala.service.KeywordPushService;
import in.koala.service.KeywordService;
import in.koala.service.UserService;
import io.swagger.models.auth.In;
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
    private final KeywordPushService keywordPushService;

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
                    convertSiteList.add(value.getCode());
                }
            }
        }

        return convertSiteList;
    }

    public List<CrawlingSite> reConvertSiteList(List<Integer> siteList) {

        List<CrawlingSite> reConvertSiteList = new ArrayList<>();

        for(Integer site : siteList) {
            for(CrawlingSite value : CrawlingSite.values()) {
                if (value.getCode().equals(site)) {
                    reConvertSiteList.add(value);
                }
            }
        }

        return reConvertSiteList;
    }

    @Override
    public void registerKeyword(Keyword keyword) throws Exception {

        Long userId = userService.getLoginUserInfo().getId();
        Timestamp createdAt = new Timestamp(System.currentTimeMillis());

        keyword.setUserId(userId);
        keyword.setCreatedAt(createdAt);

        if(keywordMapper.checkDuplicateUsersKeyword(keyword) != null){
            throw new KeywordException(ErrorMessage.DUPLICATED_KEYWORD_EXCEPTION);
        }
        else{
            keywordMapper.insertUsersKeyword(keyword);
            keywordMapper.insertUsersKeywordSite(keyword.getId(), keyword.getCreatedAt(), convertSiteList(keyword.getSiteList()));
            
            // 2022-01-03 FireBase 키워드 등록 추가
            keywordPushService.subscribe(keyword, userId);
        }
    }

    @Override
    public void deleteKeyword(String keywordName) throws Exception {
        Long userId = userService.getLoginUserInfo().getId();

        keywordMapper.deleteKeyword(userId, keywordName);

        //2022-01-03 Firebase 키워드 등록 취소
        Keyword keyword = new Keyword();
        keyword.setName(keywordName);
        keyword.setSiteList(reConvertSiteList(keywordMapper.getSiteList(userId, keywordName)));
        System.out.println(keyword.getSiteList());
        keywordPushService.unsubscribe(keyword, userId);
    }

    @Override
    public void modifyKeyword(String keywordName, Keyword keyword) {
        Long userId = userService.getLoginUserInfo().getId();

        Set<Integer> addingList = new HashSet<>(convertSiteList(keyword.getSiteList()));
        Set<Integer> addingListCopy = new HashSet<>();
        addingListCopy.addAll(addingList);
        Set<Integer> existingList = new HashSet<>(keywordMapper.getKeywordSite(userId, keywordName));

        addingList.removeAll(existingList);
        existingList.removeAll(addingListCopy);

        Long keywordId = keywordMapper.getKeywordId(userId, keywordName);
        Timestamp createdAt = new Timestamp(System.currentTimeMillis());

        if(!addingList.isEmpty()) {
            if(keywordMapper.insertUsersKeywordSite(keywordId, createdAt, new ArrayList<>(addingList)) > 0){
                System.out.println("키워드 추가 성공");
            }
            else {
                System.out.println("키워드 추가 실패");
            }
        }

        if(!existingList.isEmpty()){
            if(keywordMapper.modifyKeywordSite(existingList, keywordId) > 0){
                System.out.println("키워드 업데이트 성공");
            }
            else {
                System.out.println("키워드 업데이트 실패");
            }
        }

        keywordMapper.modifyKeyword(userId, keywordName, createdAt, keyword);
    }

    @Override
    public List<Notice> getKeywordNotice(String keywordName, String site) {

        Long userId = userService.getLoginUserInfo().getId();
        return keywordMapper.getKeywordNotice(keywordName, site, userId);
    }

    @Override
    public List<Notice> getSearchNotice(String keywordName, String site, String word) {
        Long userId = userService.getLoginUserInfo().getId();
        return keywordMapper.getSearchNotice(keywordName, site, word, userId);
    }

    @Override
    public Boolean noticeRead(String noticeId) {
        if(keywordMapper.noticeRead(noticeId) == 1){
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public void deletedNotice(String noticeId) {
        keywordMapper.deleteNotice(noticeId);
    }

    @Override
    public List<String> searchKeyword(String keyword) {
        return keywordMapper.searchKeyword(keyword);
    }
}
