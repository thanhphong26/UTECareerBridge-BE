package com.pn.career.configurations;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class ChatbotWebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(ChatbotWebSocketConfig.class);

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register only the chatbot endpoint here
        registry.addEndpoint("/ws-chatbot")
                .setAllowedOrigins("http://localhost:3000", "http://127.0.0.1:5000")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/chatbot");
        registry.setApplicationDestinationPrefixes("/app");
    }

}