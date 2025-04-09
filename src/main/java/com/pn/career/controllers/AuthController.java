package com.pn.career.controllers;

import com.pn.career.dtos.ChangePasswordDTO;
import com.pn.career.dtos.TokenDTO;
import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.exceptions.ExpiredTokenException;
import com.pn.career.exceptions.InvalidTokenException;
import com.pn.career.models.Token;
import com.pn.career.models.User;
import com.pn.career.repositories.EmployerCredentialRepository;
import com.pn.career.responses.GoogleTokenResponse;
import com.pn.career.responses.ResponseObject;
import com.pn.career.responses.ZoomTokenResponse;
import com.pn.career.services.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("${api.prefix}/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final TokenService tokenService;
    private final IUserService userService;
    private final GoogleCalendarService googleCalendarService;
    private final EmployerCredentialRepository employerCredentialRepository;
    private final CredentialService credentialService;
    @Value("${zoom.sdk.client-id}")
    private String zoomClientId;

    @Value("${zoom.sdk.redirect-uri}")
    private String zoomRedirectUri;

    @Value("${zoom.sdk.client-secret}")
    private String zoomClientSecret;

    @Value("${google.client.id}")
    private String googleClientId;

    @Value("${google.client.callback}")
    private String googleRedirectUri;
    @Value("${google.client.secret}")
    private String googleClientSecret;
    @GetMapping("/zoom/authorize")
    @PreAuthorize("hasRole('ROLE_EMPLOYER')")
    public ResponseEntity<String> authorizeZoom(@AuthenticationPrincipal Jwt jwt) {

        // Lấy thông tin người dùng
        Long userIdLong = jwt.getClaim("userId");
        Integer employerId = userIdLong != null ? userIdLong.intValue() : null;

        // Tạo URL xác thực Zoom
        String authUrl = "https://zoom.us/oauth/authorize" +
                "?response_type=code" +
                "&client_id=" + zoomClientId +
                "&redirect_uri=" + zoomRedirectUri +
                "&state=" + employerId; // Truyền ID qua state

        return ResponseEntity.ok(authUrl);
    }
    @GetMapping("/zoom/callback")
    public RedirectView handleZoomCallback(@RequestParam String code, @RequestParam String state) {
        try {
            Integer employerId = Integer.parseInt(state);
            // Đổi code lấy token
            ZoomTokenResponse tokenResponse = exchangeZoomCodeForToken(code);

            // Lưu token vào database
            credentialService.saveZoomCredentials(employerId,
                    tokenResponse.getAccessToken(),
                    tokenResponse.getRefreshToken());

            // Redirect về trang thành công
            return new RedirectView("/employer/settings?auth=zoom-success");
        } catch (Exception e) {
            log.error("Lỗi khi xử lý Zoom callback", e);
            return new RedirectView("/employer/settings?auth=zoom-error&message=" + e.getMessage());
        }
    }

    private ZoomTokenResponse exchangeZoomCodeForToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(zoomClientId, zoomClientSecret);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("code", code);
        params.add("redirect_uri", zoomRedirectUri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        return restTemplate.postForObject(
                "https://zoom.us/oauth/token",
                request,
                ZoomTokenResponse.class);
    }

    // Tạo URL xác thực Google
    @GetMapping("/google/authorize")
    @PreAuthorize("hasRole('ROLE_EMPLOYER')")
    public ResponseEntity<String> authorizeGoogle(@AuthenticationPrincipal Jwt jwt) {
        // Lấy thông tin người dùng
        Long userIdLong = jwt.getClaim("userId");
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;

        // Tạo URL xác thực Google
        String authUrl = "https://accounts.google.com/o/oauth2/auth" +
                "?response_type=code" +
                "&client_id=" + googleClientId +
                "&redirect_uri=" + googleRedirectUri +
                "&scope=https://www.googleapis.com/auth/calendar" +
                "&access_type=offline" +
                "&prompt=consent" + // Đảm bảo luôn nhận refresh token
                "&state=" + userId; // Truyền ID qua state

        return ResponseEntity.ok(authUrl);
    }

    // Xử lý callback từ Google
    @GetMapping("/google/callback")
    public RedirectView handleGoogleCallback(@RequestParam String code, @RequestParam String state) {
        try {
            // state chứa employerId
            Integer employerId = Integer.parseInt(state);
            // Đổi code lấy token
            GoogleTokenResponse tokenResponse = exchangeGoogleCodeForToken(code);

            // Lưu token vào database
//            credentialService.saveGoogleCredentials(employerId,
//                    tokenResponse.getAccessToken(),
//                    tokenResponse.getRefreshToken());

            // Redirect về trang thành công
            return new RedirectView("/employer/settings?auth=google-success");
        } catch (Exception e) {
            log.error("Lỗi khi xử lý Google callback", e);
            return new RedirectView("/employer/settings?auth=google-error&message=" + e.getMessage());
        }
    }

    private GoogleTokenResponse exchangeGoogleCodeForToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("code", code);
        params.add("redirect_uri", googleRedirectUri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        return restTemplate.postForObject(
                "https://oauth2.googleapis.com/token",
                request,
                GoogleTokenResponse.class);
    }


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
