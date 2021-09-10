package in.koala.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import in.koala.domain.test.Test;

import java.util.List;

public interface TestService {
    public List<Test> getListByAnnotation(Long cursor, Long limit);
    public List<Test> getListByXML(Long cursor, Long limit);
    public Test create();
    public void rollback() throws JsonProcessingException;
}
