package com.pn.career.services;

import com.pn.career.components.JWTTokenUtil;
import com.pn.career.dtos.TokenDTO;
import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.exceptions.ExpiredTokenException;
import com.pn.career.exceptions.InvalidTokenException;
import com.pn.career.models.Token;
import com.pn.career.models.User;
import com.pn.career.repositories.TokenRepository;
import com.pn.career.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@AllArgsConstructor
public class TokenService implements ITokenService{
    private static final int MAX_TOKENS = 2;
    private final TokenRepository tokenRepository;
    private final JWTTokenUtil jwtTokenUtil;
    private JwtDecoder jwtDecoder;
    private final Logger logger= LoggerFactory.getLogger(TokenService.class);
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    @Override
    public Token addToken(User user, TokenDTO token) {
        List<Token> tokenList=tokenRepository.findByUser(user);
        int tokenCount=tokenList.size();
        if(tokenCount> MAX_TOKENS){
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
    @Transactional
    public Token refreshToken(String refreshToken,UserDetailsImpl userDetails) throws Exception {
        Token existingToken = tokenRepository.findByRefreshToken(refreshToken);
        if (existingToken == null) {
            throw new DataNotFoundException("Refresh token does not exist");
        }
        if (existingToken.getRefreshExpirationDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(existingToken);
            throw new ExpiredTokenException("Refresh token is expired");
        }
        // Validate the refresh token
        Jwt jwt;
        try {
            jwt = jwtDecoder.decode(refreshToken);
        } catch (JwtException e) {
            throw new InvalidTokenException("Invalid refresh token", e);
        }
        // Check if the user details match the token
        if (!jwt.getSubject().equals(userDetails.getUsername())) {
            throw new InvalidTokenException("Token does not match user details");
        }
        // Create a new Authentication object
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // Generate new token pair
        TokenDTO newTokenPair = jwtTokenUtil.generateTokenPair(authentication);

        // Update token in database
        existingToken.setToken(newTokenPair.getAccessToken());
        existingToken.setRefreshToken(newTokenPair.getRefreshToken());
        existingToken.setExpirationDate(convertToLocalDateTime(jwtDecoder.decode(newTokenPair.getAccessToken()).getExpiresAt()));
        existingToken.setRefreshExpirationDate(convertToLocalDateTime(jwtDecoder.decode(newTokenPair.getRefreshToken()).getExpiresAt()));

        return tokenRepository.save(existingToken);
    }

    @Override
    public void invalidateUserTokens(Integer userId) throws DataNotFoundException {
        User user=userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy thông tin hợp lệ"));
        List<Token> validUserTokens = tokenRepository.findByUser(user);
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    @Override
    public boolean isTokenRevoked(String token) {
        return tokenRepository.findByToken(token)
                .map(Token::isRevoked)
                .orElse(false);
    }

    private LocalDateTime convertToLocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}
