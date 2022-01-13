package in.koala.interceptor;

import in.koala.domain.ChatMessage;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ChatInterceptor implements ChannelInterceptor {
    @Resource(name="redisTemplate")
    private SetOperations<String, String> setOps;

    @Resource(name="redisTemplate")
    private ListOperations<String, String> listOps;

    @Override
    public void postSend(Message message, MessageChannel channel, boolean sent){
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String token = accessor.getFirstNativeHeader("token");

        //TODO : 문서 추가 후 중단 구문 삭제
        if(token == null) return;
        switch (accessor.getCommand()){
            case CONNECT:
                Long memCnt = listOps.rightPush(token, accessor.getSessionId());
                if(memCnt == 1){ setOps.add("member", token); }
                break;
            case DISCONNECT:
                listOps.remove(token, 0, accessor.getSessionId());
                if(listOps.size(token) <= 0) setOps.remove("member", token);
                break;
        }
    }
}
