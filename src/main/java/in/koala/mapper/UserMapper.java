package in.koala.mapper;

import in.koala.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    void userJoin(User user);
}
