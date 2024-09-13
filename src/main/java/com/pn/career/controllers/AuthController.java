package com.pn.career.controllers;

import com.pn.career.dtos.TokenDTO;
import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.exceptions.ExpiredTokenException;
import com.pn.career.exceptions.InvalidTokenException;
import com.pn.career.models.Token;
import com.pn.career.models.User;
import com.pn.career.services.IUserService;
import com.pn.career.services.TokenService;
import com.pn.career.services.UserDetailsImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
