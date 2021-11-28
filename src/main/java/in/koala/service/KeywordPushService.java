package in.koala.service;

import in.koala.domain.Crawling;

import java.util.List;

public interface KeywordPushService {

    List<Crawling> pushKeyword(String deviceToken);
}
