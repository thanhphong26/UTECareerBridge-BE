package com.pn.career.controllers;

import com.pn.career.components.LocalizationUtils;
import com.pn.career.dtos.*;
import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.models.Employer;
import com.pn.career.models.User;
import com.pn.career.responses.EmployerResponse;
import com.pn.career.responses.LoginResponse;
import com.pn.career.responses.ResponseObject;
import com.pn.career.responses.StudentResponse;
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
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<ResponseObject> registerEmployer(@Valid @RequestBody EmployerRegistrationDTO employerRegistrationDTO, BindingResult result) throws Exception {
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
        User user = userService.employerRegister(employerRegistrationDTO);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.CREATED)
                .data(StudentResponse.fromUser(user))
                .message("Account employer registration successful")
                .build());
    }
    @PostMapping("/login")
    public ResponseEntity<ResponseObject> login(
            @Valid @RequestBody StudentLoginDTO studentLoginDTO,
            HttpServletRequest request
    ) throws Exception {
        TokenDTO token=userService.employerLogin(studentLoginDTO);
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
    @PostMapping("/update-company-profile")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYER')")
    public ResponseEntity<ResponseObject> updateCompanyProfile( @RequestHeader("Authorization") String authorizationHeader,
                                                                @RequestParam("company_name") String companyName,
                                                                @RequestParam("company_address") String companyAddress,
                                                                @RequestParam("company_logo") MultipartFile companyLogo,
                                                                @RequestParam("company_website") String companyWebsite,
                                                                @RequestParam("company_description") String companyDescription,
                                                                @RequestParam("background_image") MultipartFile backgroundImage,
                                                                @RequestParam("video_introduction") MultipartFile videoIntroduction,
                                                                @RequestParam("company_size") int companySize,
                                                                @RequestParam("business_certificate") String businessCertificate,
                                                                @RequestParam("industry_id") int industryId) throws DataNotFoundException {
        // Remove "Bearer " prefix from the Authorization header
        logger.debug("Received request to update company profile with authorization header: {}", authorizationHeader);
        String token = authorizationHeader.startsWith("Bearer ")
                ? authorizationHeader.substring(7)
                : authorizationHeader;
        // Fetch the user details from the serviceUs
        User user=null;
        try {
            logger.debug("User details fetched: {}", user);
            user = userService.getUserDetailsFromToken(token);
        } catch (Exception e) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.USER_DOES_NOT_EXISTS));
        }
        EmployerUpdateDTO employerUpdateDTO = EmployerUpdateDTO.builder()
                .companyName(companyName)
                .companyAddress(companyAddress)
                .companyLogo(companyLogo)
                .companyWebsite(companyWebsite)
                .companyDescription(companyDescription)
                .backgroundImage(backgroundImage)
                .videoIntroduction(videoIntroduction)
                .companySize(companySize)
                .businessCertificate(businessCertificate)
                .industryId(industryId)
                .build();

        Employer employer = employerService.updateEmployer(user.getUserId(), employerUpdateDTO);
        logger.debug("Employer updated successfully: {}", employer);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Update company profile successfully")
                .data(EmployerResponse.fromUser(employer))
                .status(HttpStatus.OK)
                .build());
    }
}
