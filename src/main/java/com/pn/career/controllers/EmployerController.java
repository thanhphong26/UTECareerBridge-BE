package com.pn.career.controllers;

import com.pn.career.components.LocalizationUtils;
import com.pn.career.dtos.*;
import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.models.Employer;
import com.pn.career.models.User;
import com.pn.career.responses.EmployerResponse;
import com.pn.career.responses.LoginResponse;
import com.pn.career.responses.ResponseObject;
import com.pn.career.services.IEmployerService;
import com.pn.career.services.ITokenService;
import com.pn.career.services.IUserService;
import com.pn.career.utils.MessageKeys;
import com.pn.career.utils.ValidationUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("${api.prefix}/employers")
public class EmployerController {
    private static final Logger logger = LoggerFactory.getLogger(EmployerController.class);

    private final IUserService userService;
    private final IEmployerService employerService;
    private final ITokenService tokenService;
    private final LocalizationUtils localizationUtils;
    private final JwtDecoder jwtDecoder;
    @PostMapping("/register")
    public ResponseEntity<ResponseObject> registerEmployer(@Valid @RequestBody EmployerRegisterDTO employerRegistrationDTO, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            String combinedErrorMessage = String.join(", ", errorMessages);

            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message(combinedErrorMessage)
                    .build());
        }
        if (employerRegistrationDTO.getEmail() == null || employerRegistrationDTO.getEmail().trim().isBlank()) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message("At least email is required")
                    .build());
        } else {
            //Email not blank
            if (!ValidationUtils.isValidEmail(employerRegistrationDTO.getEmail())) {
                return ResponseEntity.badRequest().body(ResponseObject.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .data(null)
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.EMAIL_IS_INVALID))
                        .build());
            }
        }
        if (employerRegistrationDTO.getPhoneNumber() == null || employerRegistrationDTO.getPhoneNumber().isBlank()) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message("At phone number is required")
                    .build());
        } else {
            //phone number not blank
            if (!ValidationUtils.isValidPhoneNumber(employerRegistrationDTO.getPhoneNumber())) {
                return ResponseEntity.badRequest().body(ResponseObject.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .data(null)
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.PHONE_IS_INVALID))
                        .build());
            }
        }
        if (!employerRegistrationDTO.getPassword().equals(employerRegistrationDTO.getRetypePassword())) {
            //registerResponse.setMessage();
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.PASSWORD_NOT_MATCH))
                    .build());
        }
        User user = userService.registerUser(employerRegistrationDTO, "employer");
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.CREATED)
                .data(EmployerResponse.fromUser(user))
                .message(localizationUtils.getLocalizedMessage(MessageKeys.EMPLOYER_REGISTER_SUCCESSFULLY))
                .build());
    }
    @PostMapping("/login")
    public ResponseEntity<ResponseObject> login(
            @Valid @RequestBody LoginDTO studentLoginDTO,
            HttpServletRequest request
    ) throws Exception {
        TokenDTO token=userService.userLogin(studentLoginDTO,"employer");
        String userAgent = request.getHeader("User-Agent");
        User userDetail = userService.getUserDetailsFromToken(token.getAccessToken());
        //Token jwtToken = tokenService.addToken(userDetail, token); /* Sử dụng refresh token*/

        LoginResponse loginResponse = LoginResponse.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                .token(token.getAccessToken())
                .tokenType("Bearer")
                .refreshToken(token.getRefreshToken())
                .username(userDetail.getEmail() != null ? userDetail.getEmail() : userDetail.getPhoneNumber())
                .id(userDetail.getUserId())
                .roles(userDetail.getRole())
                .build();
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                .data(loginResponse)
                .status(HttpStatus.OK)
                .build());
    }
    @PostMapping("/update-company-profile")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYER')")
    public ResponseEntity<ResponseObject> updateCompanyProfile( @AuthenticationPrincipal Jwt principal,
                                                                 @Valid @ModelAttribute EmployerUpdateDTO employerUpdateDTO) throws DataNotFoundException {
        Long userIdLong = principal.getClaim("userId");
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;

        Employer employer = employerService.updateEmployer(userId, employerUpdateDTO);
        logger.debug("Employer updated successfully: {}", employer);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.EMPLOYER_UPDATE_SUCCESSFULLY))
                .data(EmployerResponse.fromUser(employer))
                .status(HttpStatus.OK)
                .build());
    }
    @GetMapping("/company-general-info")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYER')")
    public ResponseEntity<ResponseObject> getCompanyGeneralInfo(@AuthenticationPrincipal Jwt principal) throws DataNotFoundException {
        try {
            Long userIdLong = principal.getClaim("userId");
            Integer userId = userIdLong != null ? userIdLong.intValue() : null;
            Employer employer = employerService.getEmployerById(userId);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.EMPLOYER_GET_INFO_SUCCESSFULLY))
                    .data(EmployerResponse.fromUser(employer))
                    .status(HttpStatus.OK)
                    .build());
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseObject.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message(e.getMessage())
                    .build());
        } catch (JwtException e) {
            // Xử lý trường hợp token không hợp lệ hoặc đã hết hạn
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseObject.builder()
                    .status(HttpStatus.UNAUTHORIZED)
                    .message("Token JWT không hợp lệ hoặc đã hết hạn")
                    .build());
        } catch (Exception e) {
            // Xử lý các lỗi khác
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("Đã xảy ra lỗi không mong muốn")
                    .build());
        }
    }
}
