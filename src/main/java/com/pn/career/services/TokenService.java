package com.pn.career.services;

import com.pn.career.components.JWTTokenUtil;
import com.pn.career.dtos.TokenDTO;
import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.exceptions.ExpiredTokenException;
import com.pn.career.models.Token;
import com.pn.career.models.User;
import com.pn.career.repositories.TokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TokenService implements ITokenService{
    private static final int MAX_TOKENS = 2;
    private final TokenRepository tokenRepository;
    private final JWTTokenUtil jwtTokenUtil;
    private JwtDecoder jwtDecoder;
    @Override
    public Token addToken(User user, TokenDTO token) {
        List<Token> tokenList=tokenRepository.findByUser(user);
        int tokenCount=tokenList.size();
        if(tokenCount>=MAX_TOKENS){
            Token oldestToken = tokenList.get(0);
            tokenRepository.delete(oldestToken);
        }
        LocalDateTime expirationDateAccessToken = convertToLocalDateTime(jwtDecoder.decode(token.getAccessToken()).getExpiresAt());
        LocalDateTime expirationDateRefreshToken = convertToLocalDateTime(jwtDecoder.decode(token.getRefreshToken()).getExpiresAt());
        Token newToken = Token.builder()
                .user(user)
                .token(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .revoked(false)
                .expired(false)
                .tokenType("Bearer")
                .expirationDate(LocalDateTime.from(expirationDateAccessToken))
                .refreshExpirationDate(LocalDateTime.from(expirationDateRefreshToken))
                .build();
        return tokenRepository.save(newToken);
    }

    @Override
    public Token refreshToken(String refreshToken,UserDetailsImpl userDetails) throws Exception {
        Token existingToken = tokenRepository.findByRefreshToken(refreshToken);
        if(existingToken == null) {
            throw new DataNotFoundException("Refresh token does not exist");
        }
        if(existingToken.getRefreshExpirationDate().compareTo(LocalDateTime.now()) < 0){
            tokenRepository.delete(existingToken);
            throw new ExpiredTokenException("Refresh token is expired");
        }
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(), null, userDetails.getAuthorities());
        TokenDTO token = jwtTokenUtil.generateTokenPair(authentication);

        LocalDateTime expirationDateAccessToken = convertToLocalDateTime(jwtDecoder.decode(token.getAccessToken()).getExpiresAt());
        LocalDateTime expirationDateRefreshToken = convertToLocalDateTime(jwtDecoder.decode(token.getRefreshToken()).getExpiresAt());

        existingToken.setExpirationDate(LocalDateTime.from(expirationDateAccessToken));
        existingToken.setToken(token.getAccessToken());
        existingToken.setRefreshToken(token.getRefreshToken());
        existingToken.setRefreshExpirationDate(LocalDateTime.from(expirationDateRefreshToken));
        return existingToken;
    }
    private LocalDateTime convertToLocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}
