package in.koala.config;

import in.koala.interceptor.ChatInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import javax.annotation.Resource;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Resource
    ChatInterceptor chatInterceptor;

    // enableSimpleBroker : Simple Message Broker를 사용하며, /sub로 시작하는 url로 구독
    // setApplicationDestinationPrefixes : 메시지를 보낼 때 맨 앞에 포함되는 url
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry){
        registry.enableSimpleBroker("/sub");
        registry.setApplicationDestinationPrefixes("/pub");
    }

    // Websocket 또는 SockJS가 웹소켓 핸드셰이크 커넥션을 생성할 경로 지정
    // sockJS를 사용하기 위해서 withSockJS() 포함
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration){
        registration.interceptors(chatInterceptor);
    }
}
