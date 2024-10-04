package com.pn.career.controllers;

import com.pn.career.dtos.ChangePasswordDTO;
import com.pn.career.dtos.TokenDTO;
import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.exceptions.ExpiredTokenException;
import com.pn.career.exceptions.InvalidTokenException;
import com.pn.career.models.Token;
import com.pn.career.models.User;
import com.pn.career.responses.ResponseObject;
import com.pn.career.services.IUserService;
import com.pn.career.services.TokenService;
import com.pn.career.services.UserDetailsImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/auth")
@AllArgsConstructor
public class AuthController {
    private final TokenService tokenService;
    private final IUserService userService;
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        String refreshToken = null;
        if(cookies!=null){
            for(Cookie cookie: cookies){
                if(cookie.getName().equals("refreshToken")){
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        try{
            User user = userService.getUserDetailsFromToken(refreshToken);
            UserDetailsImpl userDetails = new UserDetailsImpl(user);
            Token newToken = tokenService.refreshToken(refreshToken, userDetails);

            TokenDTO tokenDTO = TokenDTO.builder()
                    .accessToken(newToken.getToken())
                    .refreshToken(newToken.getRefreshToken())
                    .build();

            Cookie newFreshToken = new Cookie("refreshToken",newToken.getRefreshToken());
            newFreshToken.setHttpOnly(true);
            newFreshToken.setMaxAge(7*60*24*60);
            newFreshToken.setPath("/");
            response.addCookie(newFreshToken);

            return ResponseEntity.ok(tokenDTO);
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body("Refresh token does not exist");
        } catch (ExpiredTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token has expired");
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while refreshing the token");
        }
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseObject> forgotPassword(@RequestParam String email) throws DataNotFoundException {
        try{
            userService.initiatePasswordReset(email);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Chúng tôi đã gửi hướng dẫn đặt lại mật khẩu qua email của bạn. Vui lòng kiểm tra email của bạn để tiếp tục")
                    .status(HttpStatus.OK)
                    .data(null)
                    .build());
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @PostMapping("/reset-password")
    public ResponseEntity<ResponseObject> resetPassword(@RequestBody ChangePasswordDTO changePasswordDTO) throws Exception {
        try {
            userService.resetPassword(changePasswordDTO.getToken(), changePasswordDTO.getPassword());
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .data(null)
                    .message("Mật khẩu của bạn đã được thay đổi thành công. Vui lòng đăng nhập bằng mật khẩu mới để tiếp tục")
                    .build());
        } catch (InvalidTokenException e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message("Có lỗi xảy ra trong quá trình xác thực vui lòng thực hiện lại")
                    .build());
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<ResponseObject> logout(HttpServletRequest request, HttpServletResponse response, @AuthenticationPrincipal Jwt jwt) throws DataNotFoundException {
        Long userIdLong = jwt.getClaim("userId");
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .message("Đã xảy ra lỗi trong quá trình đăng xuất. Vui lòng thử lại sau.")
                            .build()
            );
        }
        tokenService.invalidateUserTokens(userId);
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Đăng xuất thành công")
                .build());
    }
}
