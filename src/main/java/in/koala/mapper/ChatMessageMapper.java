package in.koala.mapper;

import in.koala.domain.ChatMessage;
import in.koala.domain.Criteria;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageMapper {
    List<ChatMessage> getMessageList(@Param("criteria") Criteria criteria);
    int insertMessage(ChatMessage message);
    List<ChatMessage> searchMessage(@Param("criteria") Criteria criteria, @Param("word")String word);
}
