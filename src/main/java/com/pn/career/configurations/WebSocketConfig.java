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

    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic","/queue", "/user");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }
//
//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(new ChannelInterceptor() {
//            @Override
//            public Message<?> preSend(Message<?> message, MessageChannel channel) {
//                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//                logger.debug("WebSocket message type: {}", accessor.getCommand());
//
//                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//                    try {
//                        String authToken = accessor.getFirstNativeHeader("Authorization");
//                        logger.debug("Auth token received (full): {}", authToken);
//
//                        if (authToken != null && authToken.startsWith("Bearer ")) {
//                            authToken = authToken.substring(7);
//                            logger.debug("Processing JWT authentication with token: {}", authToken);
//                            try {
//                                Jwt jwt = jwtDecoder.decode(authToken);
//                                accessor.setUser(new JwtAuthenticationToken(jwt));
//                                logger.debug("JWT authentication successful. Claims: {}", jwt.getClaims());
//                            } catch (Exception e) {
//                                logger.error("JWT decode error: {}", e.getMessage(), e);
//                                throw new MessageDeliveryException(message, "Invalid JWT token", e);
//                            }
//                        } else {
//                            logger.warn("Invalid or missing Authorization header: {}", authToken);
//                            throw new MessageDeliveryException(message, "Invalid or missing Authorization header");
//                        }
//                    } catch (Exception e) {
//                        logger.error("Error processing WebSocket connection: {}", e.getMessage(), e);
//                        throw new MessageDeliveryException(message, "Error processing WebSocket connection", e);
//                    }
//                }
//                return message;
//            }
//        });
//    }
}
