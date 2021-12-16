package in.koala.service;

import in.koala.domain.Keyword;

import java.util.List;

public interface KeywordService {
    List<Keyword> myKeywordList();
    void registerKeyword(String keyword, short site, boolean isImportant);
    void deleteKeyword(String keywordId);
    void modifyKeyword(String keywordId, String keywordName);
}
