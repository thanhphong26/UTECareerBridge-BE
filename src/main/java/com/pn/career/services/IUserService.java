package com.pn.career.services;

import com.pn.career.dtos.EmployerRegistrationDTO;
import com.pn.career.dtos.StudentLoginDTO;
import com.pn.career.dtos.StudentRegistrationDTO;
import com.pn.career.dtos.TokenDTO;
import com.pn.career.models.User;

public interface IUserService {
    User studentRegister(StudentRegistrationDTO studentRegistrationDTO) throws Exception;
    User employerRegister(EmployerRegistrationDTO employerRegistrationDTO) throws Exception;
    TokenDTO userLogin(StudentLoginDTO studentLoginDTO) throws Exception;
    TokenDTO employerLogin(StudentLoginDTO studentLoginDTO) throws Exception;
    User getUserDetailsFromToken(String token) throws Exception;
    /*User getUserDetailsFromRefreshToken(String token) throws Exception;
    User updateUser(Long userId, UpdateUserDTO updatedUserDTO) throws Exception;*/
}
