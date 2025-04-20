package com.pn.career.configurations;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);
    private final JwtDecoder jwtDecoder;
    private final Map<String, String> userRefreshTokens = new ConcurrentHashMap<>();
    // RestTemplate for making HTTP requests
    private final RestTemplate restTemplate = new RestTemplate();
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:3000", "http://127.0.0.1:5500") // Thay bằng origin của client
                .withSockJS();
        //notification
        registry.addEndpoint("/ws-notifications")
                .setAllowedOrigins("http://localhost:3000", "http://127.0.0.1:5500", "http://localhost:8080")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue", "/notifications", "/user");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {

                    String authorizationHeader = accessor.getFirstNativeHeader("Authorization");
                    String refreshToken = accessor.getFirstNativeHeader("RefreshToken");

                    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                        String token = authorizationHeader.substring(7);
                        logger.info("Token received: {}", token);

                        try {
                            // First, try to decode the provided token
                            Jwt jwt = jwtDecoder.decode(token);
                            String userId = jwt.getSubject();

                            // Store the refresh token for later use if provided
                            if (refreshToken != null && !refreshToken.isEmpty()) {
                                userRefreshTokens.put(userId, refreshToken);
                            }

                            JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt);
                            accessor.setUser(authentication);

                            // Log successful connection
                            String userRole = jwt.getClaimAsString("roles");
                            logger.info("User connected: ID={}, Role={}", userId, userRole);

                        } catch (JwtException e) {
                            logger.warn("JWT token expired or invalid: {}", e.getMessage());

                            // Attempt to refresh the token if we have a refresh token
                            if (refreshToken != null && !refreshToken.isEmpty()) {
                                try {
                                    // Call your refresh token endpoint
                                    ResponseEntity<Map> response = refreshTokenRequest(refreshToken);

                                    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                                        // Extract the new access token
                                        String newAccessToken = (String) response.getBody().get("accessToken");
                                        String newRefreshToken = (String) response.getBody().get("refreshToken");

                                        if (newAccessToken != null) {
                                            logger.info("Token refreshed successfully");

                                            // Decode the new token
                                            Jwt newJwt = jwtDecoder.decode(newAccessToken);
                                            String userId = newJwt.getSubject();

                                            // Update stored refresh token
                                            if (newRefreshToken != null) {
                                                userRefreshTokens.put(userId, newRefreshToken);
                                            }

                                            JwtAuthenticationToken authentication = new JwtAuthenticationToken(newJwt);
                                            accessor.setUser(authentication);

                                            // Add the new token to response headers (if needed)
                                            accessor.setNativeHeader("New-Access-Token", newAccessToken);

                                            // Log successful refresh and connection
                                            String userRole = newJwt.getClaimAsString("roles");
                                            logger.info("User reconnected after token refresh: ID={}, Role={}", userId, userRole);

                                            return message;
                                        }
                                    }
                                } catch (Exception refreshError) {
                                    logger.error("Failed to refresh token", refreshError);
                                }
                            }

                            // If we reach here, both token decode and refresh attempts failed
                            logger.error("Authentication failed and token refresh was not possible");
                            throw new MessageDeliveryException("Authentication failed: " + e.getMessage());
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

    /**
     * Call the token refresh API endpoint
     */
    private ResponseEntity<Map> refreshTokenRequest(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "refreshToken=" + refreshToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Call your refresh token endpoint
        return restTemplate.exchange(
                "http://localhost:8080/api/v1/auth/refresh",
                HttpMethod.POST,
                entity,
                Map.class
        );
    }
}
