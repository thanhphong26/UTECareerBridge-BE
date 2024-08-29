package com.pn.career.controllers;

import com.pn.career.components.LocalizationUtils;
import com.pn.career.dtos.StudentLoginDTO;
import com.pn.career.dtos.StudentRegistrationDTO;
import com.pn.career.dtos.TokenDTO;
import com.pn.career.models.User;
import com.pn.career.responses.LoginResponse;
import com.pn.career.responses.ResponseObject;
import com.pn.career.responses.StudentResponse;
import com.pn.career.services.ITokenService;
import com.pn.career.services.IUserService;
import com.pn.career.utils.MessageKeys;
import com.pn.career.utils.ValidationUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("${api.prefix}/users")
public class UserController {
    private final IUserService userService;
    private final ITokenService tokenService;
    private final LocalizationUtils localizationUtils;
    @PostMapping("/register")
    public ResponseEntity<ResponseObject> registerStudent(@RequestBody StudentRegistrationDTO studentRegistrationDTO) throws Exception {
        if (studentRegistrationDTO.getEmail() == null || studentRegistrationDTO.getEmail().trim().isBlank()) {
            if (studentRegistrationDTO.getPhoneNumber() == null || studentRegistrationDTO.getPhoneNumber().isBlank()) {
                return ResponseEntity.badRequest().body(ResponseObject.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .data(null)
                        .message("At least email or phone number is required")
                        .build());
            } else {
                //phone number not blank
                if (!ValidationUtils.isValidPhoneNumber(studentRegistrationDTO.getPhoneNumber())) {
                    throw new Exception("Invalid phone number");
                }
            }
        } else {
            //Email not blank
            if (!ValidationUtils.isValidEmail(studentRegistrationDTO.getEmail())) {
                throw new Exception("Invalid email format");
            }
        }
        if (!studentRegistrationDTO.getPassword().equals(studentRegistrationDTO.getRetypePassword())) {
            //registerResponse.setMessage();
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.PASSWORD_NOT_MATCH))
                    .build());
        }
        User user = userService.studentRegister(studentRegistrationDTO);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.CREATED)
                .data(StudentResponse.fromUser(user))
                .message("Account registration successful")
                .build());
    }
    @PostMapping("/login")
    public ResponseEntity<ResponseObject> login(
            @Valid @RequestBody StudentLoginDTO studentLoginDTO,
            HttpServletRequest request
    ) throws Exception {
        TokenDTO token=userService.userLogin(studentLoginDTO);
        String userAgent = request.getHeader("User-Agent");
        User userDetail = userService.getUserDetailsFromToken(token.getAccessToken());
        //Token jwtToken = tokenService.addToken(userDetail, token); /* Sử dụng refresh token*/

        LoginResponse loginResponse = LoginResponse.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                .token(token.getAccessToken())
                .tokenType("Bearer")
                .refreshToken(token.getRefreshToken())
                .username(userDetail.getUsername())
                .roles(userDetail.getAuthorities().stream().map(item -> item.getAuthority()).toList())
                .id(userDetail.getUserId())
                .build();
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Login successfully")
                .data(loginResponse)
                .status(HttpStatus.OK)
                .build());
    }
}
