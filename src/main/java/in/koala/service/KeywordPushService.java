package in.koala.service;

public interface KeywordPushService {

    void pushKeyword(String deviceToken);
    void pushKeywordAtOnce(String deviceToken) throws Exception;
}
