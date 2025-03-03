package com.pn.career.services;

import java.io.IOException;
import java.util.Map;

public interface IAuthService {
    String generateAuthUrl(String loginType, String role);
    Map<String, Object> authenticateAndFetchProfile(String code, String loginType) throws IOException;
}
