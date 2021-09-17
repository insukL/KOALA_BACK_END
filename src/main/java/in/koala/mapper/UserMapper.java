package in.koala.mapper;

import in.koala.domain.naverLogin.NaverUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    public String test();
    public String findId(String account);
    public void signUp(NaverUser naverUser);
}
