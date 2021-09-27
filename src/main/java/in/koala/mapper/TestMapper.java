package in.koala.mapper;

import in.koala.domain.test.Test;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestMapper {
    @Select("SELECT * FROM tb_test_info ORDER BY created_at LIMIT #{cursor}, #{limit}")
    List<Test> getTestListAnnotation(@Param("cursor") Long cursor, @Param("limit") Long limit);

    @Insert("INSERT INTO tb_test_info (TEXT, NUM) " +
            "VALUES (#{text}, #{num})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Long.class)
    void testCreate(Test user);

    List<Test> getTestListByXML(@Param("cursor") Long cursor, @Param("limit") Long limit);
}
