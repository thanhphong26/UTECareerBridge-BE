package com.pn.career.utils;

import org.springframework.security.oauth2.jwt.Jwt;

public class JWTCheck {
    public static Integer getUserIdFromJWT(Jwt jwt) {
        if (jwt == null) {
            return null;
        }
        Long userIdLong = jwt.getClaim("userId");
        return userIdLong != null ? userIdLong.intValue() : null;
    }
}
