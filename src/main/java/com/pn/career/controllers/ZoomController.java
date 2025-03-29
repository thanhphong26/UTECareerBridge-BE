package com.pn.career.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pn.career.models.EmployerCredential;
import com.pn.career.responses.MeetingResponse;
import com.pn.career.services.CredentialService;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import okhttp3.RequestBody;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

@RestController
@RequestMapping("${api.prefix}/zoom")
@RequiredArgsConstructor
public class ZoomController {
    private static final String ZOOM_MEETINGS_URL = "https://api.zoom.us/v2/users/me/meetings";
    private static final String ZOOM_TOKEN_URL = "https://zoom.us/oauth/token";
    private final CredentialService credentialService;
    @Value("${zoom.sdk.client-id}")
    private String clientId;

    @Value("${zoom.sdk.client-secret}")
    private String clientSecret;

    @Value("${zoom.sdk.account-id}")
    private String accountId;

    // Phương thức tạo cuộc họp sử dụng Server-to-Server OAuth
    @PostMapping("/create-meeting")
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_EMPLOYER')")
    public ResponseEntity<MeetingResponse> createMeeting(@AuthenticationPrincipal Jwt jwt) throws IOException {
        Long userIdLong = jwt.getClaim("userId");
        Integer employerId = userIdLong != null ? userIdLong.intValue() : null;
        OkHttpClient client = new OkHttpClient();

        try {
            Optional<EmployerCredential> credentialOpt = credentialService.findByEmployerId(employerId);

            String accessToken;
            if (credentialOpt.isPresent() && credentialOpt.get().getZoomAccessToken() != null) {
                // Sử dụng token đã lưu
                accessToken = credentialOpt.get().getZoomAccessToken();

                // Kiểm tra xem token có hợp lệ không bằng cách gọi API
                if (!isValidToken(accessToken)) {
                    // Nếu token không hợp lệ, làm mới token
                    accessToken = refreshZoomToken(employerId, credentialOpt.get().getZoomRefreshToken());
                }
            } else {
                // Nếu nhà tuyển dụng chưa có token, sử dụng token của hệ thống
                accessToken = getZoomAccessToken();
            }

            String jsonBody = "{"
                    + "\"topic\": \"Interview\","
                    + "\"type\": 2,"
                    + "\"duration\": 60,"
                    + "\"settings\": {"
                    + "  \"host_video\": true,"
                    + "  \"participant_video\": true,"
                    + "  \"allow_participants_to_share\": true,"
                    + "  \"join_before_host\": true"
                    + "}"
                    + "}";

            Request request = new Request.Builder()
                    .url(ZOOM_MEETINGS_URL)
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/json")
                    .post(RequestBody.create(MediaType.parse("application/json"), jsonBody))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    System.out.println("Response code: " + response.code());
                    System.out.println("Response message: " + response.message());
                    String responseBody = response.body().string();
                    System.out.println("Response body: " + responseBody);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new MeetingResponse("Error: " + response.code() + " - " + response.message(), null, null));
                }

                String responseBody = response.body().string();
                MeetingResponse meeting = parseMeetingResponse(responseBody);
                return ResponseEntity.ok(meeting);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MeetingResponse("Error: " + e.getMessage(), null, null));
        }
    }

    // Phương thức để lấy access token thông qua Server-to-Server OAuth
    private String getZoomAccessToken() throws IOException {
        OkHttpClient client = new OkHttpClient();

        // Tạo chuỗi xác thực Basic Authentication
        String credentials = clientId + ":" + clientSecret;
        String base64Credentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        // Tạo body yêu cầu
        RequestBody body = new FormBody.Builder()
                .add("grant_type", "account_credentials")
                .add("account_id", accountId)
                .build();

        Request request = new Request.Builder()
                .url(ZOOM_TOKEN_URL)
                .header("Authorization", "Basic " + base64Credentials)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body().string();
                throw new IOException("Failed to get access token: " + response.code() + " - " + errorBody);
            }

            String responseBody = response.body().string();
            org.json.JSONObject jsonResponse = new JSONObject(responseBody);
            return jsonResponse.getString("access_token");
        }
    }

    private MeetingResponse parseMeetingResponse(String jsonResponse) {
        // Implement the JSON parsing logic here
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jsonResponse, MeetingResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private boolean isValidToken(String accessToken) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.zoom.us/v2/users/me")
                .header("Authorization", "Bearer " + accessToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        } catch (Exception e) {
            return false;
        }
    }

    // Làm mới token Zoom
    private String refreshZoomToken(Integer employerId, String refreshToken) throws IOException {
        OkHttpClient client = new OkHttpClient();

        // Tạo chuỗi xác thực Basic Authentication
        String credentials = clientId + ":" + clientSecret;
        String base64Credentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        // Tạo body yêu cầu
        RequestBody body = new FormBody.Builder()
                .add("grant_type", "refresh_token")
                .add("refresh_token", refreshToken)
                .build();

        Request request = new Request.Builder()
                .url(ZOOM_TOKEN_URL)
                .header("Authorization", "Basic " + base64Credentials)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to refresh token: " + response.code());
            }

            String responseBody = response.body().string();
            JSONObject jsonResponse = new JSONObject(responseBody);
            String newAccessToken = jsonResponse.getString("access_token");
            String newRefreshToken = jsonResponse.getString("refresh_token");

            // Lưu token mới vào cơ sở dữ liệu
            credentialService.saveZoomCredentials(employerId, newAccessToken, newRefreshToken);

            return newAccessToken;
        }
    }
}