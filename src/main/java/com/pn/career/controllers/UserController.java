package com.pn.career.controllers;

import com.pn.career.components.LocalizationUtils;
import com.pn.career.dtos.LoginDTO;
import com.pn.career.dtos.StudentRegisterDTO;
import com.pn.career.dtos.TokenDTO;
import com.pn.career.dtos.UpdateUserDTO;
import com.pn.career.models.Token;
import com.pn.career.models.User;
import com.pn.career.responses.*;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
                        .message("Không được để trống email hoặc số điện thoại")
                        .build());
            } else {
                //phone number not blank
                if (!ValidationUtils.isValidPhoneNumber(studentRegistrationDTO.getPhoneNumber())) {
                    throw new Exception("Số điện thoại không hợp lệ");
                }
            }
        } else {
            //Email not blank
            if (!ValidationUtils.isValidEmail(studentRegistrationDTO.getEmail())) {
                throw new Exception("Email không hợp lệ");
            }
        }
        if (!studentRegistrationDTO.getPassword().equals(studentRegistrationDTO.getRetypePassword())) {
            //registerResponse.setMessage();
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message("Mật khẩu không khớp")
                    .build());
        }
        User user = userService.registerUser(studentRegistrationDTO,"student");
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.CREATED)
                .data(StudentResponse.fromUser(user))
                .message("Đăng ký tài khoản thành công")
                .build());

    }
    @PostMapping("/login")
    public ResponseEntity<ResponseObject> login(
            @Valid @RequestBody LoginDTO studentLoginDTO,
            HttpServletRequest request, HttpServletResponse response
    ) throws Exception {
        try{
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
                    .message("Đăng nhập thành oông")
                    .token(token.getAccessToken())
                    .tokenType("Bearer")
                    .refreshToken(token.getRefreshToken())
                    .username((userDetail.getEmail()!=null)?userDetail.getEmail():userDetail.getPhoneNumber())
                    .id(userDetail.getUserId())
                    .roles(userDetail.getRole())
                    .build();
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Đăng nhập thành oông")
                    .data(loginResponse)
                    .status(HttpStatus.OK)
                    .build());
        }catch(BadCredentialsException e){
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        }
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
    @GetMapping("/get-all-users")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> getAllUsers(
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name = "role", defaultValue = "") String role,
            @RequestParam(name="sorting", required = false, defaultValue = "createdAt") String sorting,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
    ) {
        try{
            PageRequest pageRequest = PageRequest.of(page, size);
            Page<UserResponse> users = userService.getAllUsers(keyword, role, sorting, pageRequest);
            return ResponseEntity.ok().body(
                    ResponseObject.builder()
                            .message("Lấy danh sách người dùng thành công")
                            .data(UserListResponse.builder()
                                    .userResponses(users.getContent())
                                    .totalPages(users.getTotalPages())
                                    .build())
                            .status(HttpStatus.OK)
                            .build()
            );
        }catch (Exception e){
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message(e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }
    @GetMapping("/get-user/{userId}")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYER') || hasAuthority('ROLE_STUDENT') || hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> getUserById(@AuthenticationPrincipal Jwt jwt, @PathVariable Integer userId){
        try{
            Long userIdLong = jwt.getClaim("userId");
            String role = jwt.getClaim("roles");
            Integer id = userIdLong != null ? userIdLong.intValue() : null;
            if(id == null || !id.equals(userId) && !role.equals("ADMIN")){
                return ResponseEntity.badRequest().body(
                        ResponseObject.builder()
                                .message("Bạn không có quyền truy cập thông tin người dùng này")
                                .status(HttpStatus.BAD_REQUEST)
                                .build()
                );
            }
            UserResponse userResponse = userService.getUserBydId(userId);
            return ResponseEntity.ok().body(
                    ResponseObject.builder()
                            .message("Lấy thông tin người dùng thành công")
                            .data(userResponse)
                            .status(HttpStatus.OK)
                            .build()
            );
        }catch (Exception e){
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message(e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }
    @DeleteMapping("/delete/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> deleteUser(@PathVariable Integer userId){
        try{
            userService.deleteUser(userId);
            return ResponseEntity.ok().body(
                    ResponseObject.builder()
                            .message("Xóa người dùng thành công")
                            .status(HttpStatus.OK)
                            .build()
            );
        }catch (Exception e){
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message(e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }
    @PutMapping("/update-user/{userId}")
    @PreAuthorize("hasAuthority('ROLE_STUDENT') || hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> updateUser(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Integer userId,
            @RequestBody UpdateUserDTO user
    ){
        try{
            Long userIdLong = jwt.getClaim("userId");
            String role = jwt.getClaim("roles");
            Integer id = userIdLong != null ? userIdLong.intValue() : null;
            if(id == null || !id.equals(userId) && !role.equals("ADMIN")){
                return ResponseEntity.badRequest().body(
                        ResponseObject.builder()
                                .message("Bạn không có quyền truy cập thông tin người dùng này")
                                .status(HttpStatus.BAD_REQUEST)
                                .build()
                );
            }
            UserResponse userResponse = userService.updateUser(userId, user);
            return ResponseEntity.ok().body(
                    ResponseObject.builder()
                            .message("Cập nhật thông tin người dùng thành công")
                            .data(userResponse)
                            .status(HttpStatus.OK)
                            .build()
            );
        }catch (Exception e){
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message(e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

}
