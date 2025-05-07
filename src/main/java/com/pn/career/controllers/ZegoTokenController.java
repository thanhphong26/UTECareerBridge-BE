package com.pn.career.controllers;

import com.pn.career.responses.ResponseObject;
import com.pn.career.services.ZegoTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/zegocloud")
@RequiredArgsConstructor
public class ZegoTokenController {
    private final ZegoTokenService tokenService;
    @GetMapping("/token")
    public ResponseEntity<ResponseObject> getToken( @RequestParam Number roomId, @AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("userId");
        String token = tokenService.generateToken(userId, roomId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseObject.
                        builder()
                        .status(HttpStatus.OK)
                        .message("Token generated successfully")
                        .data(Map.of("token", token))
                        .build());
    }
}
