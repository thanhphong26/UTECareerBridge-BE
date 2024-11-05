package com.pn.career.services;

import com.pn.career.dtos.LoginDTO;
import com.pn.career.dtos.RegistrationDTO;
import com.pn.career.dtos.TokenDTO;
import com.pn.career.dtos.UpdateUserDTO;
import com.pn.career.models.User;
import com.pn.career.responses.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface IUserService {
    User registerUser(RegistrationDTO registrationDTO, String roleName) throws Exception;
    TokenDTO userLogin(LoginDTO studentLoginDTO, String... allowedRoles) throws Exception;
    User getUserDetailsFromToken(String token) throws Exception;
    void initiatePasswordReset(String email) throws Exception;
    void resetPassword(String resetToken, String newPassword) throws Exception;
    void blockOrEnable(Integer userId, boolean active) throws Exception;
    Page<UserResponse> getAllUsers(String keyword, String roleName, String sorting, PageRequest pageRequest);
    UserResponse getUserBydId(Integer userId);
<<<<<<< Updated upstream
    void deleteUser(Integer userId);
=======

    UserResponse updateUser(Integer userId, UpdateUserDTO user);
>>>>>>> Stashed changes
}
