package com.pn.career.controllers;

import com.pn.career.components.LocalizationUtils;
import com.pn.career.dtos.LoginDTO;
import com.pn.career.dtos.StudentRegisterDTO;
import com.pn.career.dtos.TokenDTO;
import com.pn.career.models.Token;
import com.pn.career.models.User;
import com.pn.career.responses.LoginResponse;
import com.pn.career.responses.ResponseObject;
import com.pn.career.responses.StudentResponse;
import com.pn.career.services.ITokenService;
import com.pn.career.services.IUserService;
import com.pn.career.services.UserService;
import com.pn.career.utils.MessageKeys;
import com.pn.career.utils.ValidationUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController
@AllArgsConstructor
@RequestMapping("${api.prefix}/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final IUserService userService;
    private final ITokenService tokenService;
    private final LocalizationUtils localizationUtils;
    @PostMapping("/register")
    public ResponseEntity<ResponseObject> registerStudent(@RequestBody StudentRegisterDTO studentRegistrationDTO) throws Exception {
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
        User user = userService.registerUser(studentRegistrationDTO,"student");
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.CREATED)
                .data(StudentResponse.fromUser(user))
                .message(localizationUtils.getLocalizedMessage(MessageKeys.STUDENT_REGISTER_SUCCESSFULLY))
                .build());

    }
    @PostMapping("/login")
    public ResponseEntity<ResponseObject> login(
            @Valid @RequestBody LoginDTO studentLoginDTO,
            HttpServletRequest request, HttpServletResponse response
    ) throws Exception {
        TokenDTO token=userService.userLogin(studentLoginDTO,"student","admin");
        String userAgent = request.getHeader("User-Agent");
        User userDetail = userService.getUserDetailsFromToken(token.getAccessToken());
        Token jwtToken = tokenService.addToken(userDetail, token); /* Sử dụng refresh token*/
        Cookie refreshToken=new Cookie("refreshToken",token.getRefreshToken());
        refreshToken.setHttpOnly(true);
        refreshToken.setMaxAge(7*24*60*60);
        refreshToken.setPath("/");
        response.addCookie(refreshToken);

        LoginResponse loginResponse = LoginResponse.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                .token(token.getAccessToken())
                .tokenType("Bearer")
                .refreshToken(token.getRefreshToken())
                .username((userDetail.getEmail()!=null)?userDetail.getEmail():userDetail.getPhoneNumber())
                .id(userDetail.getUserId())
                .roles(userDetail.getRole())
                .build();
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                .data(loginResponse)
                .status(HttpStatus.OK)
                .build());
    }
    @PutMapping("/block/{userId}/{active}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> blockUser(
            @Valid @PathVariable Integer userId,
            @Valid @PathVariable Integer active
    ) throws Exception {
        userService.blockOrEnable(userId, active >0);
        String message = active > 0 ? "Đã kích hoạt người dùng thành công." : "Đã chặn người dùng thành công.";
        return ResponseEntity.ok().body(
                ResponseObject.builder()
                        .message(message)
                        .status(HttpStatus.OK)
                        .build()
        );
    }


}
