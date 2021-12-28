package in.koala.mapper;

import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface NoticeMapper {

    void insertNotice(Map map);
}
