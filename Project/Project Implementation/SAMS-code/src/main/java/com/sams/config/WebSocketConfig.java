package com.sams.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * websocket config for realtime stuff (notifications, messages, etc)
 * using STOMP protocol - its like http but for websockets
 *
 * this was tricky to get working with our JWT auth
 * make sure sockjs is working if you have issues in old browsers
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // simple in-memory broker - works fine for our use case
        // /topic = broadcast to everyone subscribed
        // /queue = send to specific user
        config.enableSimpleBroker("/topic", "/queue");

        // prefix for messages coming FROM clients
        config.setApplicationDestinationPrefixes("/app");

        // prefix for user-specific messages
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // this is the endpoint clients connect to
        // using sockjs as fallback for browsers that dont support ws
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // TODO: restrict this in production maybe?
                .withSockJS();
    }
}
