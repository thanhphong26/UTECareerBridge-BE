package com.pn.career.services;

import com.pn.career.dtos.TokenDTO;
import com.pn.career.models.Token;
import com.pn.career.models.User;
import com.pn.career.repositories.TokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TokenService implements ITokenService{
    private final TokenRepository tokenRepository;
    @Override
    public Token addToken(User user, TokenDTO token, boolean isMobileDevice) {
        return null;
    }

    @Override
    public Token refreshToken(String refreshToken, User user) throws Exception {
        return null;
    }
}
