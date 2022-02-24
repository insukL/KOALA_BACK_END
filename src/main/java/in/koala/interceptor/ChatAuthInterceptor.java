package in.koala.interceptor;

import in.koala.enums.TokenType;
import in.koala.util.JwtUtil;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.annotation.Resource;
import java.util.Map;

@Component
public class ChatAuthInterceptor implements HandshakeInterceptor {

    @Resource
    private JwtUtil jwtUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        int idx = request.getURI().toString().indexOf('=')+1;
        String token = request.getURI().toString().substring(idx);

        return jwtUtil.validateToken("Bearer "+token, TokenType.SOCKET) != null;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception ex) {
    }
}
