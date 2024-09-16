package com.pn.career.services;

import com.pn.career.dtos.LoginDTO;
import com.pn.career.dtos.RegistrationDTO;
import com.pn.career.dtos.TokenDTO;
import com.pn.career.models.User;

public interface IUserService {
    User registerUser(RegistrationDTO registrationDTO, String roleName) throws Exception;
    TokenDTO userLogin(LoginDTO studentLoginDTO, String... allowedRoles) throws Exception;
    User getUserDetailsFromToken(String token) throws Exception;
    void initiatePasswordReset(String email) throws Exception;
    void resetPassword(String resetToken, String newPassword) throws Exception;
}
