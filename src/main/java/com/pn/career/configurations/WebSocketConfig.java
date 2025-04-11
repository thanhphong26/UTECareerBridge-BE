package com.pn.career.configurations;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);
    private final JwtDecoder jwtDecoder;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:3000", "http://127.0.0.1:5500") // Thay bằng origin của client
                .withSockJS();
        // end point for chatbot
        registry.addEndpoint("/ws-chatbot")
                .setAllowedOrigins("http://localhost:3000", "http://127.0.0.1:5000")
                .withSockJS();
        //notification
        registry.addEndpoint("/ws-notifications")
                .setAllowedOrigins("http://localhost:3000", "http://127.0.0.1:5500", "http://localhost:8080")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue", "/chatbot", "/notifications", "/user");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                logger.info("Message received: {}", accessor.getCommand());
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String authorizationHeader = accessor.getFirstNativeHeader("Authorization");

                    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                        String token = authorizationHeader.substring(7);
                        logger.info("Token: {}", token);
                        try {
                            Jwt jwt = jwtDecoder.decode(token);
                            JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt);
                            accessor.setUser(authentication);

                            // Log successful connection with role
                            String userRole = jwt.getClaimAsString("roles");
                            String userId = jwt.getSubject();
                            logger.info("User connected: ID={}, Role={}", userId, userRole);

                        } catch (Exception e) {
                            logger.error("Invalid JWT token in WebSocket connection", e);
                            throw new MessageDeliveryException("Invalid authentication token");
                        }
                    } else {
                        logger.warn("No authentication token provided for WebSocket connection");
                        throw new MessageDeliveryException("No authentication token provided");
                    }
                }

                return message;
            }
        });
    }
}
