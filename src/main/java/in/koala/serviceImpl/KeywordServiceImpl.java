package in.koala.serviceImpl;

import in.koala.domain.Keyword;
import in.koala.domain.Notice;
import in.koala.domain.user.User;
import in.koala.enums.CrawlingSite;
import in.koala.enums.CrawlingSiteKorean;
import in.koala.enums.ErrorMessage;
import in.koala.enums.UserType;
import in.koala.exception.CriticalException;
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

    @Override
    public List<Keyword> myKeywordList() {
        Long userId = getLoginUserInfo().getId();
        return keywordMapper.myKeywordList(userId);
    }

    @Override
    public Boolean registerKeyword(Keyword keyword) throws Exception{

        User user = getLoginUserInfo();
        Long userId = user.getId();
        UserType userType = user.getUser_type();

        checkKeywordMaxNumberByUserType(userType, userId);

        keyword.setUserId(userId);

        checkDuplicateUsersKeyword(keyword);

        insertUsersKeyword(keyword);

        insertUsersKeywordSite(keyword);

        fireBaseSubscribe(keyword, userId);

        return true;
    }

    @Override
    public Boolean deleteKeyword(String keywordName) throws Exception {
        Long userId = getLoginUserInfo().getId();

        Keyword keyword = new Keyword();
        keyword.setName(keywordName);
        keyword.setSiteList(getKeywordsSiteList(userId, keywordName));
        fireBaseUnSubscribe(keyword, userId);

        deleteKeyword(userId, keywordName);
        return true;
    }

    @Override
    public Boolean modifyKeyword(String keywordName, Keyword keyword) throws Exception {

        Long userId = getLoginUserInfo().getId();

        checkDuplicateUsersKeyword(keyword);

        Set<CrawlingSite> addingList = new HashSet<>(keyword.getSiteList());
        Set<CrawlingSite> addingListCopy = copySiteList(addingList);

        Set<CrawlingSite> existingList = new HashSet<>(keywordMapper.getKeywordSite(userId, keywordName));
        Set<CrawlingSite> existingListCopy = copySiteList(existingList);

        addingList.removeAll(existingList);
        existingList.removeAll(addingListCopy);

        Long keywordId = getKeywordId(userId, keywordName);

        insertNewKeywordSite(keywordId, addingList);

        modifyExistingKeywordSite(keywordId, existingList);

        modifyKeyword(userId, keywordName, keyword);

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

        return true;
    }

    @Override
    public List<String> searchKeyword(String keyword) {
        return keywordMapper.searchKeyword(keyword);
    }

    @Override
    public List<Notice> getSearchNotice(String keywordName, String site, String word) {
        Long userId = getLoginUserInfo().getId();
        return keywordMapper.getSearchNotice(keywordName, site, word, userId);
    }

    @Override
    public List<Notice> getKeywordNotice(String keywordName, String site) {
        Long userId = getLoginUserInfo().getId();
        return keywordMapper.getKeywordNotice(keywordName, site, userId);
    }

    @Override
    public Boolean deleteNotice(List<Integer> noticeList) {
        if(keywordMapper.deleteNotice(noticeList) == 1)
            return true;
        else
            return false;
    }

    @Override
    public Boolean deleteNoticeUndo(List<Integer> noticeList) {
        if(keywordMapper.deleteNoticeUndo(noticeList) == 1)
            return true;
        else
            return false;
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
    public List<String> recommendKeyword() {
        return keywordMapper.recommendKeyword();
    }

    @Override
    public List<String> getSiteList(String keywordName) {
        Long userId = getLoginUserInfo().getId();
        List<String> siteList = convertSiteToKorean(keywordMapper.getSiteList(userId, keywordName));
        return siteList;
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

    private User getLoginUserInfo(){
        return userService.getLoginUserInfo();
    }

    private List<String> convertSiteToKorean(List<CrawlingSite> crawlingSiteList){
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

    private void checkKeywordMaxNumberByUserType(UserType userType, Long userId){
        if(userType.equals(UserType.NON)){
            if(keywordMapper.countKeywordNum(userId) >= NON_USER_MAX_KEYWORD_NUM)
                throw new KeywordException(ErrorMessage.EXCEED_MAXIMUM_KEYWORD_NUMBER);
        }
        else{
            if(keywordMapper.countKeywordNum(userId) >= NORMAL_USER_MAX_KEYWORD_NUM)
                throw new KeywordException(ErrorMessage.EXCEED_MAXIMUM_KEYWORD_NUMBER);
        }
    }

    private void checkDuplicateUsersKeyword(Keyword keyword){
        if(keywordMapper.checkDuplicateUsersKeyword(keyword) != null){
            throw new KeywordException(ErrorMessage.DUPLICATED_KEYWORD_EXCEPTION);
        }
    }

    private void insertUsersKeyword(Keyword keyword){
        if(keywordMapper.insertUsersKeyword(keyword) != 1)
            throw new CriticalException(ErrorMessage.DATA_INSERT_ERROR);
    }

    private void insertUsersKeywordSite(Keyword keyword){
        if(keywordMapper.insertUsersKeywordSite(keyword.getId(), keyword.getSiteList()) != keyword.getSiteList().size())
            throw new CriticalException(ErrorMessage.DATA_INSERT_ERROR);
    }

    private void fireBaseSubscribe(Keyword keyword, Long userId) throws Exception{
        keywordPushService.subscribe(keyword, userId);
    }

    private List<CrawlingSite> getKeywordsSiteList(Long userId, String keywordName){
        return keywordMapper.getSiteList(userId, keywordName);
    }

    private void fireBaseUnSubscribe(Keyword keyword, Long userId) throws Exception{
        keywordPushService.unsubscribe(keyword, userId);
    }

    private void deleteKeyword(Long userId, String keywordName){
        keywordMapper.deleteKeyword(userId, keywordName);
    }

    private void insertNewKeywordSite(Long keywordId, Set<CrawlingSite> addingList){
        if(!addingList.isEmpty()) {
            if(keywordMapper.insertUsersKeywordSite(keywordId, new ArrayList<>(addingList)) <= 0)
                throw new KeywordException(ErrorMessage.FAIL_TO_INSERT_NEW_KEYWORD_SITE);
        }
    }

    private void modifyExistingKeywordSite(Long keywordId, Set<CrawlingSite> existingList){
        if(!existingList.isEmpty()){
            if(keywordMapper.modifyKeywordSite(existingList, keywordId) <= 0)
                throw new KeywordException(ErrorMessage.FAIL_TO_MODIFY_EXISTING_KEYWORD_SITE);
        }
    }

    private Long getKeywordId(Long userId, String keywordName){
        return keywordMapper.getKeywordId(userId, keywordName);
    }

    private void modifyKeyword(Long userId, String keywordName, Keyword keyword){
        keywordMapper.modifyKeyword(userId, keywordName, keyword);
    }

    private Set copySiteList(Set<CrawlingSite> list){
        Set<CrawlingSite> copyList = new HashSet<>();
        copyList.addAll(list);
        return copyList;
    }
}
