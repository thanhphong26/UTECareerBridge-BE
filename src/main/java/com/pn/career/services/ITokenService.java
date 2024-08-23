package com.pn.career.services;

import com.pn.career.dtos.TokenDTO;
import com.pn.career.models.Token;
import com.pn.career.models.User;

public interface ITokenService {
    Token addToken(User user, TokenDTO token);
    Token refreshToken(String refreshToken, User user) throws Exception;
}
