package com.pn.career.controllers;

import com.pn.career.components.LocalizationUtils;
import com.pn.career.dtos.*;
import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.models.*;
import com.pn.career.responses.*;
import com.pn.career.services.IBenefitDetailService;
import com.pn.career.services.IEmployerService;
import com.pn.career.services.ITokenService;
import com.pn.career.services.IUserService;
import com.pn.career.utils.MessageKeys;
import com.pn.career.utils.ValidationUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.AuthenticationEntryPoint;
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
    private final IBenefitDetailService benefitDetailService;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

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
            HttpServletRequest request, HttpServletResponse response
    ) throws Exception {
        TokenDTO token=userService.userLogin(studentLoginDTO,"employer");
        String userAgent = request.getHeader("User-Agent");
        User userDetail = userService.getUserDetailsFromToken(token.getAccessToken());
        Token jwtToken = tokenService.addToken(userDetail, token); /* Sử dụng refresh token*/
        Cookie refreshToken=new Cookie("refreshToken",token.getRefreshToken());
        refreshToken.setHttpOnly(true);
        refreshToken.setMaxAge(7*24*60*60);
        refreshToken.setPath("/");
        response.addCookie(refreshToken);
        LoginResponse loginResponse = LoginResponse.builder()
                .message("Đăng nhập thành công")
                .token(token.getAccessToken())
                .tokenType("Bearer")
                .refreshToken(token.getRefreshToken())
                .username(userDetail.getEmail() != null ? userDetail.getEmail() : userDetail.getPhoneNumber())
                .id(userDetail.getUserId())
                .roles(userDetail.getRole())
                .build();
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Đăng nhập thành công")
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
    @PostMapping("/update-profile")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYER')")
    public ResponseEntity<ResponseObject> updateProfile(@AuthenticationPrincipal Jwt principal, @Valid @RequestBody UpdateProfileDTO updateProfileDTO) throws DataNotFoundException {
        Long userIdLong = principal.getClaim("userId");
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        Employer employer = employerService.updateProfile(userId, updateProfileDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Cập nhật thông tin công ty thành công")
                .data(EmployerResponse.fromUser(employer))
                .status(HttpStatus.OK)
                .build());
    }
    @PostMapping("/update-profile/change-password")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYER')")
    public ResponseEntity<ResponseObject> updatePassword(@AuthenticationPrincipal Jwt principal, @Valid @RequestBody UpdatePasswordDTO updatePasswordDTO) throws DataNotFoundException {
        Long userIdLong = principal.getClaim("userId");
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        employerService.updatePassword(userId, updatePasswordDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Đổi mật khẩu thành công")
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
                    .message("Lấy thông tin công ty thành công")
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
    @GetMapping("/get-all-employers")
    public ResponseEntity<ResponseObject> getAllEmployers( @RequestParam(defaultValue = "") String keyword,
                                                           @RequestParam(defaultValue = "0", name = "industry_id") Integer industryId,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int limit) {
        int totalPage = 0;
        PageRequest pageRequest=PageRequest.of(page,limit);
        Page<EmployerResponse> employers = employerService.getAllEmployers(keyword, industryId, pageRequest);
        if (employers.getTotalPages() > 0) {
            totalPage = employers.getTotalPages();
        }
        List<EmployerResponse> employerResponses = employers.getContent();
        if (employerResponses.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseObject.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message("Không tìm thấy công ty nào")
                    .build());
        }
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy danh sách công ty thành công")
                .data(EmployerListResponse.builder()
                        .employerResponses(employerResponses)
                        .totalPages(totalPage)
                        .build())
                .status(HttpStatus.OK)
                .build());
    }
    @DeleteMapping("/delete-benefits/{benefitId}")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYER')")
    public ResponseEntity<ResponseObject> deleteBenefit(@AuthenticationPrincipal Jwt jwt, @Valid @PathVariable Integer benefitId) throws DataNotFoundException {
        Long userIdLong = jwt.getClaim("userId");
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        BenefitDetailId benefitDetailId = BenefitDetailId.builder()
                .benefitId(benefitId)
                .employerId(userId)
                .build();
        BenefitDetail benefitDetail=benefitDetailService.findBenefitDetailById(benefitDetailId);
        if(benefitDetailService.deleteBenefitDetail(benefitDetail)){
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Xóa phúc lợi thành công")
                    .status(HttpStatus.OK)
                    .build());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder()
                .message("Xóa phúc lợi thất bại")
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build());
    }
    @GetMapping("/get-all-benefits")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYER')")
    public ResponseEntity<ResponseObject> getAllBenefitDetailByEmployer(@AuthenticationPrincipal Jwt jwt) throws DataNotFoundException {
        Long userIdLong = jwt.getClaim("userId");
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        Employer employer=employerService.getEmployerById(userId);
        List<BenefitDetail> benefitDetails=benefitDetailService.findAllByEmployerId(employer);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy danh sách phúc lợi của công ty thành công")
                .status(HttpStatus.OK)
                .data(BenefitDetailResponse.fromBenefitDetailResponse(benefitDetails))
                .build());
    }
    @PostMapping("/legal-info")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYER')")
    public ResponseEntity<ResponseObject> addBusinessCertificate(@AuthenticationPrincipal Jwt jwt, @RequestBody MultipartFile businessCertificate) throws DataNotFoundException {
        try{
            Long userIdLong = jwt.getClaim("userId");
            Integer userId = userIdLong != null ? userIdLong.intValue() : null;
            employerService.addBusinessCertificate(userId,businessCertificate);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Tải lên giấy phép kinh doanh thành công. Vui lòng chờ duyệt từ phía quản trị viên")
                    .status(HttpStatus.OK)
                    .build());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder()
                    .message("Đã xảy ra lỗi khi tải lên giấy phép kinh doanh. Vui lòng thử lại sau")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build());
        }
    }
    @PutMapping("/admin/{employerId}/legal-info/approve")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> approveEmployer(@PathVariable Integer employerId) {
        try {
            employerService.approveEmployer(employerId);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Duyệt giấy phép kinh doanh của nhà tuyển dụng thành công")
                    .status(HttpStatus.OK)
                    .build());
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseObject.builder()
                    .message("Không tìm thấy nhà tuyển dụng tương ứng")
                    .status(HttpStatus.NOT_FOUND)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder()
                    .message("Đã xảy ra lỗi khi duyệt nhà tuyển dụng. Vui lòng thử lại sau")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build());
        }
    }
    @PutMapping("/admin/{employerId}/legal-info/reject")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> rejectEmployer(@PathVariable Integer employerId, @RequestBody RejectDTO rejectDTO) {
        try {
            employerService.rejectEmployer(employerId, rejectDTO.reason());
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Từ chối nhà tuyển dụng thành công")
                    .status(HttpStatus.OK)
                    .build());
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseObject.builder()
                    .message("Không tìm thấy nhà tuyển dụng tương ứng")
                    .status(HttpStatus.NOT_FOUND)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder()
                    .message("Đã xảy ra lỗi khi từ chối nhà tuyển dụng. Vui lòng thử lại sau")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build());
        }
    }

}
