package in.koala.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    // sub로 시작하는 stomp 메시지의 destination 헤더는 컨트롤러 객체의 MessageMapping으로 라우팅
    // 내장된 메시지 브로커를 사용해 클라이언트에게 /pub으로 시작하는 destination 헤더를 가진 메시지를
    // 브로커로 라우팅, Simple Message Broker는 특별한 의미 부여하지 않음
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry){
        registry.enableSimpleBroker("/sub");
        registry.setApplicationDestinationPrefixes("/pub");
    }

    // Websocket 또는 SockJS가 웹소켓 핸드셰이크 커넥션을 생성할 경로 지정
    @Override
    public void  registerStompEndpoints(StompEndpointRegistry registry){
        registry.addEndpoint("/ws").setAllowedOrigins("*")
                .withSockJS();
    }
}
