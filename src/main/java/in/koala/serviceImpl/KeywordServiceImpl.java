package in.koala.serviceImpl;

import in.koala.domain.Crawling;
import in.koala.domain.Keyword;
import in.koala.domain.Notice;
import in.koala.domain.user.User;
import in.koala.enums.CrawlingSite;
import in.koala.enums.CrawlingSiteKorean;
import in.koala.enums.ErrorMessage;
import in.koala.enums.UserType;
import in.koala.exception.KeywordException;
import in.koala.mapper.KeywordMapper;
import in.koala.service.KeywordPushService;
import in.koala.service.KeywordService;
import in.koala.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class KeywordServiceImpl implements KeywordService {

    private final KeywordMapper keywordMapper;
    private final UserService userService;
    private final KeywordPushService keywordPushService;

    private static final int NORMAL_USER_MAX_KEYWORD_NUM = 10;
    private static final int NON_USER_MAX_KEYWORD_NUM = 5;

    public List<String> convertSiteToKorean(List<CrawlingSite> crawlingSiteList){
        List<String> koreanSiteList = new ArrayList<>();
        for(CrawlingSite site : crawlingSiteList){
            for(CrawlingSiteKorean value : CrawlingSiteKorean.values()){
                if(value.toString().equals(site.toString())){
                    koreanSiteList.add(value.getSiteName());
                }
            }
        }
        return koreanSiteList;
    }

    @Override
    public List<Keyword> myKeywordList() {
        Long userId = userService.getLoginUserInfo().getId();
        return keywordMapper.myKeywordList(userId);
    }

    @Override
    public void registerKeyword(Keyword keyword) throws Exception {

        User user = userService.getLoginUserInfo();
        Long userId = user.getId();
        UserType userType = user.getUser_type();

        if(userType.equals(UserType.NON)){
            if(keywordMapper.countKeywordNum(userId) >= NON_USER_MAX_KEYWORD_NUM)
                throw new KeywordException(ErrorMessage.EXCEED_MAXIMUM_KEYWORD_NUMBER);
        }
        else{
            if(keywordMapper.countKeywordNum(userId) >= NORMAL_USER_MAX_KEYWORD_NUM)
                throw new KeywordException(ErrorMessage.EXCEED_MAXIMUM_KEYWORD_NUMBER);
        }

        keyword.setUserId(userId);

        if(keywordMapper.checkDuplicateUsersKeyword(keyword) != null){
            throw new KeywordException(ErrorMessage.DUPLICATED_KEYWORD_EXCEPTION);
        }
        else{
            keywordMapper.insertUsersKeyword(keyword);
            keywordMapper.insertUsersKeywordSite(keyword.getId(), keyword.getSiteList());

            // 2022-01-03 FireBase 키워드 등록 추가
            keywordPushService.subscribe(keyword, userId);
        }
    }

    @Override
    public void deleteKeyword(String keywordName) throws Exception {
        Long userId = userService.getLoginUserInfo().getId();

        //2022-01-03 Firebase 키워드 등록 취소
        Keyword keyword = new Keyword();
        keyword.setName(keywordName);
        keyword.setSiteList(keywordMapper.getSiteList(userId, keywordName));
        keywordPushService.unsubscribe(keyword, userId);

        keywordMapper.deleteKeyword(userId, keywordName);
    }

    @Override
    public void modifyKeyword(String keywordName, Keyword keyword) throws Exception {

        Long userId = userService.getLoginUserInfo().getId();

        if(keywordMapper.checkDuplicateUsersKeyword(keyword) != null){
            throw new KeywordException(ErrorMessage.DUPLICATED_KEYWORD_EXCEPTION);
        }

        Set<CrawlingSite> addingList = new HashSet<>(keyword.getSiteList());
        Set<CrawlingSite> addingListCopy = new HashSet<>();
        addingListCopy.addAll(addingList);

        Set<CrawlingSite> existingList = new HashSet<>(keywordMapper.getKeywordSite(userId, keywordName));
        Set<CrawlingSite> existingListCopy = new HashSet<>();
        existingListCopy.addAll(existingList);

        addingList.removeAll(existingList);
        existingList.removeAll(addingListCopy);

        Long keywordId = keywordMapper.getKeywordId(userId, keywordName);

        if(!addingList.isEmpty()) {
            if(keywordMapper.insertUsersKeywordSite(keywordId, new ArrayList<>(addingList)) > 0){
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

        keywordMapper.modifyKeyword(userId, keywordName, keyword);

        //2022-01-06 Firebase 키워드 수정 로직
        List<CrawlingSite> oldSite = new ArrayList<>(existingList);
        List<CrawlingSite> newSite = new ArrayList<>(addingList);

        if(keywordName.equals(keyword.getName())){
            keywordPushService.modifySubscription(oldSite, newSite, userId, keywordName);
        }
        else{
            Keyword oldKeyword = new Keyword();
            oldKeyword.setName(keywordName);
            oldKeyword.setSiteList(new ArrayList<>(existingListCopy));

            keywordPushService.subscribe(keyword, userId);
            keywordPushService.unsubscribe(oldKeyword, userId);
        }
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
    public void deletedNotice(List<Integer> noticeList) {
        keywordMapper.deleteNotice(noticeList);
    }

    @Override
    public List<String> searchKeyword(String keyword) {
        return keywordMapper.searchKeyword(keyword);
    }

    @Override
    public List<String> recommendKeyword() {
        return keywordMapper.recommendKeyword();
    }

    @Override
    public List<String> recommendSite() {
        List<CrawlingSite> siteList = keywordMapper.recommendSite();
        List<String> koreanSiteList = convertSiteToKorean(siteList);
        return koreanSiteList;
    }

    @Override
    public List<String> searchSite(String site) {
        List<String> result = new ArrayList<>();
        for(CrawlingSiteKorean value : CrawlingSiteKorean.values()){
            String siteName = value.getSiteName();
            if(siteName.contains(site)){
                result.add(siteName);
            }
        }
        return result;
    }
}
