package cn.hanli.mw.board.dashboard.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * 用 WebSocket 发送消息的配置类
 *
 * @author Han Li
 * Created at 3/6/2021 5:05 下午
 * Modified by Han Li at 3/6/2021 5:05 下午
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer  {

    /**
     * 允许 sockJS 透过 stomp (endpoint) 访问大屏资料
     * @param registry r
     */
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp")
                .setAllowedOrigins("*")
                .withSockJS();
    }

    /**
     * Message Broker
     * @param config c
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
    }
}
