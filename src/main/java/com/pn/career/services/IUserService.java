package com.pn.career.services;

import com.pn.career.dtos.StudentLoginDTO;
import com.pn.career.dtos.StudentRegistrationDTO;
import com.pn.career.dtos.TokenDTO;
import com.pn.career.models.Token;
import com.pn.career.models.User;

public interface IUserService {
    User studentRegister(StudentRegistrationDTO studentRegistrationDTO) throws Exception;
    TokenDTO login(StudentLoginDTO studentLoginDTO) throws Exception;
    User getUserDetailsFromToken(String token) throws Exception;
    /*User getUserDetailsFromRefreshToken(String token) throws Exception;
    User updateUser(Long userId, UpdateUserDTO updatedUserDTO) throws Exception;*/
}
