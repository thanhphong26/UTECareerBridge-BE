package com.pn.career.components;

import com.pn.career.dtos.TokenDTO;
import com.pn.career.services.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class JWTTokenUtil {
    private JwtEncoder accessTokenEncoder;
    @Qualifier("jwtRefreshTokenEncoder")
    private JwtEncoder refreshTokenEncoder;
    private JwtDecoder jwtDecoder;
    private final Logger logger= LoggerFactory.getLogger(JWTTokenUtil.class);
    private String createAccessToken(Authentication authentication) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("ute-career-bridge")
                .issuedAt(now)
                .expiresAt(now.plus(30, ChronoUnit.DAYS))
                .subject(authentication.getName())
                .claim("roles", createScope(authentication))
                .claim("userId", ((UserDetailsImpl) authentication.getPrincipal()).getUser().getUserId())
                .build();
        return accessTokenEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private String createRefreshToken(Authentication authentication) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("ute-career-bridge")
                .issuedAt(now)
                .expiresAt(now.plus(60, ChronoUnit.DAYS))
                .subject(authentication.getName())
                .claim("roles", createScope(authentication))
                .claim("userId", ((UserDetailsImpl) authentication.getPrincipal()).getUser().getUserId())
                .build();
        return refreshTokenEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private String createScope(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
    }
    public TokenDTO generateTokenPair(Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof UserDetailsImpl user)) {
            throw new BadCredentialsException(MessageFormat.format(
                    "Principal {0} is not of User type", authentication.getPrincipal().getClass()
            ));
        }
        TokenDTO tokenDTO=new TokenDTO();
        tokenDTO.setUserId(user.getUser().getUserId());
        tokenDTO.setAccessToken(createAccessToken(authentication));
        logger.info("TAccess token created for user: {}", tokenDTO.getAccessToken());
        String refreshToken;
        if(authentication.getCredentials() instanceof Jwt jwt){
            Instant now=Instant.now();
            Instant expiresAt=jwt.getExpiresAt();
            Duration duration=Duration.between(now,expiresAt);
            long daysUntilExpired=duration.toDays();
            if(daysUntilExpired<7){
                refreshToken=createRefreshToken(authentication);
            }else{
                refreshToken=jwt.getTokenValue();
            }
        }else{
            refreshToken=createRefreshToken(authentication);
        }
        tokenDTO.setRefreshToken(refreshToken);
        logger.info("TRefresh token created for user: {}", tokenDTO.getRefreshToken());
        return tokenDTO;
    }
    public Boolean isTokenExpired(String token) {
        var jwt = jwtDecoder.decode(token);
        return jwt.getExpiresAt().isBefore(Instant.now());
    }
    public String getSubjectFromToken(String token) {
        Jwt decodedJwt = jwtDecoder.decode(token);
        return decodedJwt.getSubject();
    }
    public String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
