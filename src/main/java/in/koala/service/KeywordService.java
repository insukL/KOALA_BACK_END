package in.koala.service;

import in.koala.domain.Keyword;

import java.util.List;

public interface KeywordService {
    List<Keyword> myKeywordList();
    void registerKeyword(Keyword keyword);
    void deleteKeyword(String keywordId);
    void modifyKeyword(String keywordId, String keywordName);
}
