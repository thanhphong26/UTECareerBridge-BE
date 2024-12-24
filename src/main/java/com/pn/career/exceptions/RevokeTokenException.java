package com.pn.career.exceptions;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import javax.naming.AuthenticationException;

public class RevokeTokenException extends OAuth2AuthenticationException {
    public RevokeTokenException(String message) {
        super(message);
    }
}
