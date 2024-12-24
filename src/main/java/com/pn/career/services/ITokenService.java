package com.pn.career.services;

import com.pn.career.dtos.TokenDTO;
import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.models.Token;
import com.pn.career.models.User;

public interface ITokenService {
    Token addToken(User user, TokenDTO token);
    Token refreshToken(String refreshToken,  UserDetailsImpl userDetails) throws Exception;
    void invalidateUserTokens(Integer userId) throws DataNotFoundException;
    boolean isTokenRevoked(String token);
}
