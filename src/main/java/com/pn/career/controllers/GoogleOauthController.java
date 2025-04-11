package com.pn.career.controllers;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.pn.career.responses.ResponseObject;
import com.pn.career.services.CredentialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("${api.prefix}/oauth")
@RequiredArgsConstructor
public class GoogleOauthController {
    private final CredentialService credentialService;
    @Value("${google.client.id}")
    private String googleClientId;

    @Value("${google.client.secret}")
    private String googleClientSecret;

    @Value("${google.client.callback}")
    private String redirectUri;

    @Value("${google.client.scopes}")
    private List<String> scopes;

    @GetMapping("/auth-url")
    @PreAuthorize("hasRole('ROLE_EMPLOYER')")
    public ResponseEntity<ResponseObject> getAuthorizationUrl(@AuthenticationPrincipal Jwt jwt) {
        Long userIdLong = jwt.getClaim("userId");
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        GoogleAuthorizationCodeRequestUrl url = new GoogleAuthorizationCodeRequestUrl(
                googleClientId,
                redirectUri,
                scopes)
                .setAccessType("offline")
                .setApprovalPrompt("force")
                .setState(userId.toString());

        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(Collections.singletonMap("url", url.build()))
                .build());
    }

    @GetMapping("/callback")
    public ResponseEntity<ResponseObject> handleCallback(@RequestParam String code, @RequestParam String state) {
        try {
            Integer userId = Integer.parseInt(state);

            GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance(),
                    googleClientId,
                    googleClientSecret,
                    code,
                    redirectUri)
                    .execute();

            // Save tokens to database
            credentialService.saveGoogleCredentials(
                    userId,
                    tokenResponse.getAccessToken(),
                    tokenResponse.getRefreshToken(),
                    tokenResponse.getExpiresInSeconds() != null && tokenResponse.getExpiresInSeconds() > 0
            );

            // Redirect to frontend with success message
            return ResponseEntity.status(HttpStatus.FOUND)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .message("Google authorization successful")
                            .data(null)
                            .build());
        } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .message("Error during Google authorization")
                            .data(null)
                            .build());
        }
    }
}
