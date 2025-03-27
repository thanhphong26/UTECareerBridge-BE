package com.pn.career.components;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class ZoomJwtUtil {
    @Value("${zoom.sdk.client-id}")
    private String sdkKey;
    @Value("${zoom.sdk.client-secret}")
    private String sdkSecret;

    private static String staticSdkKey;
    private static String staticSdkSecret;

    @PostConstruct
    public void init() {
        staticSdkKey = this.sdkKey;
        staticSdkSecret = this.sdkSecret;
    }

    public static String generateZoomSdkJwt(String meetingNumber, int role) {
        if (staticSdkKey == null || staticSdkSecret == null) {
            throw new IllegalStateException("Zoom SDK credentials not initialized properly");
        }

        long now = System.currentTimeMillis();
        long expiration = now + 7200 * 1000; // 2 giờ

        // Tạo đúng payload cho Zoom SDK
        Map<String, Object> claims = new HashMap<>();
        claims.put("sdkKey", staticSdkKey);
        claims.put("mn", meetingNumber);
        claims.put("role", role);
        claims.put("tokenExp", expiration / 1000);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(expiration))
                .signWith(SignatureAlgorithm.HS256, staticSdkSecret.getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    // Nếu vẫn cần JWT cho API Zoom (không khuyến khích)
    public static String generateZoomApiJwt() {
        if (staticSdkKey == null || staticSdkSecret == null) {
            throw new IllegalStateException("Zoom SDK credentials not initialized properly");
        }

        long now = System.currentTimeMillis();

        return Jwts.builder()
                .setIssuer(staticSdkKey)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + 3600 * 1000))
                .signWith(SignatureAlgorithm.HS256, staticSdkSecret.getBytes(StandardCharsets.UTF_8))
                .compact();
    }
}
