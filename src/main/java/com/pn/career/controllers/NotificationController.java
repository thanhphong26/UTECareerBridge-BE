package com.pn.career.controllers;

import com.pn.career.dtos.NotificationRequest;
import com.pn.career.models.Notification;
import com.pn.career.responses.NotificationResponse;
import com.pn.career.responses.ResponseObject;
import com.pn.career.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping("/broadcast")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> sendBroadcastNotification(@RequestBody NotificationRequest request) {
        notificationService.sendBroadcastNotification(request.getTitle(), request.getMessage());
        return ResponseEntity.ok(ResponseObject.builder().message("Thông báo đã được gửi thành công").status(HttpStatus.OK).build());
    }
    @PostMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> sendNotificationToUser(
            @PathVariable Integer userId,
            @RequestBody NotificationRequest request) {
        notificationService.sendNotificationToUser(userId, request.getTitle(), request.getMessage());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('EMPLOYER')")
    public ResponseEntity<ResponseObject> getUserNotifications(@PathVariable Integer userId, @AuthenticationPrincipal Jwt jwt, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        Long userIdLong = jwt.getClaim("userId");
        Integer id = userIdLong != null ? userIdLong.intValue() : null;
        if(id == null || !id.equals(userId)) {
            return ResponseEntity.badRequest().body(ResponseObject.builder().message("Ban khong co quyen truy cap").status(HttpStatus.BAD_REQUEST).build());
        }
        PageRequest pageRequest = PageRequest.of(page != null ? page : 0, size != null ? size : 10);
        Page<Notification> notifications = notificationService.getUserNotifications(userId, pageRequest);
        Page<NotificationResponse> notificationResponses = notifications.map(NotificationResponse::fromNotification);
        return ResponseEntity.ok(ResponseObject.builder().data(notificationResponses).status(HttpStatus.OK).build());
    }
    @GetMapping("/user/general-notification")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('EMPLOYER')")
    public ResponseEntity<ResponseObject> getBroadcastNotifications(@AuthenticationPrincipal Jwt jwt, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        Long userIdLong = jwt.getClaim("userId");
        Integer id = userIdLong != null ? userIdLong.intValue() : null;
        PageRequest pageRequest = PageRequest.of(page != null ? page : 0, size != null ? size : 10);
        Page<Notification> notifications = notificationService.getBroadcastNotifications(pageRequest);
        Page<NotificationResponse> notificationResponses = notifications.map(NotificationResponse::fromNotification);
        return ResponseEntity.ok(ResponseObject.builder().data(notificationResponses).status(HttpStatus.OK).build());
    }
    @PostMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> sendRoleNotification(
            @PathVariable String role,
            @RequestBody NotificationRequest request) {
        if (role.equals("STUDENT") || role.equals("EMPLOYER")) {
            notificationService.sendRoleNotification(role, request.getTitle(), request.getMessage());
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Thông báo đã được gửi thành công")
                    .status(HttpStatus.OK)
                    .build());
        }
        return ResponseEntity.badRequest().body(ResponseObject.builder().message("Thông tin không hợp lệ").status(HttpStatus.BAD_REQUEST).build());
    }
    @PutMapping("/{notificationId}/read")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('EMPLOYER')")
    public ResponseEntity<ResponseObject> makeNotificationAsRead(@PathVariable Integer notificationId, @AuthenticationPrincipal Jwt jwt) {
        Long userIdLong = jwt.getClaim("userId");
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        if(userId == null) {
            return ResponseEntity.badRequest().body(ResponseObject.builder().message("Ban khong co quyen truy cap").status(HttpStatus.BAD_REQUEST).build());
        }
        notificationService.makeNotificationAsRead(notificationId);
        return ResponseEntity.ok(ResponseObject.builder().message("Thông báo đã được đọc").status(HttpStatus.OK).build());
    }
    @PutMapping("/user/{userId}/read")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('EMPLOYER')")
    public ResponseEntity<ResponseObject> makeAllNotificationsAsRead(@PathVariable Integer userId, @AuthenticationPrincipal Jwt jwt) {
        Long userIdLong = jwt.getClaim("userId");
        Integer id = userIdLong != null ? userIdLong.intValue() : null;
        if(id == null || !id.equals(userId)) {
            return ResponseEntity.badRequest().body(ResponseObject.builder().message("Ban khong co quyen truy cap").status(HttpStatus.BAD_REQUEST).build());
        }
        notificationService.makeAllNotificationsAsRead(userId);
        return ResponseEntity.ok(ResponseObject.builder().message("Tất cả thông báo đã được đọc").status(HttpStatus.OK).build());
    }
    @GetMapping("/user/{userId}/count")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('EMPLOYER')")
    public ResponseEntity<ResponseObject> getUnreadNotificationCount(@PathVariable Integer userId, @AuthenticationPrincipal Jwt jwt) {
        Long userIdLong = jwt.getClaim("userId");
        Integer id = userIdLong != null ? userIdLong.intValue() : null;
        if(id == null || !id.equals(userId)) {
            return ResponseEntity.badRequest().body(ResponseObject.builder().message("Ban khong co quyen truy cap").status(HttpStatus.BAD_REQUEST).build());
        }
        Long count = notificationService.getUnreadNotificationCount(userId);
        return ResponseEntity.ok(ResponseObject.builder().data(count).status(HttpStatus.OK).build());
    }
}
