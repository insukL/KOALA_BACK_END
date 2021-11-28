package in.koala.service;

public interface FcmTestService {
    String sendTokenTest(String token) throws Exception;
    String sendTopicTest(String token) throws Exception;
    String sendConditionTest(String Condition) throws Exception;
}
