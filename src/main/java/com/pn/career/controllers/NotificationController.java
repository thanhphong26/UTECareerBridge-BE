package com.pn.career.controllers;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.pn.career.dtos.NotificationDTO;
import com.pn.career.dtos.TopicSubscriptionDTO;
import com.pn.career.models.Notification;
import com.pn.career.responses.NotificationResponse;
import com.pn.career.services.FCMService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final FCMService fcmService;
    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribeToTopic(@RequestBody TopicSubscriptionDTO request) {
        try {
            fcmService.subribeToTopic(request.getToken(), request.getTopic());
            return ResponseEntity.ok("Subscribed to topic successfully");
        } catch (FirebaseMessagingException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
    @PostMapping("/token")
    public ResponseEntity<String> sendNotificationToToken(@RequestBody NotificationDTO request) {
        try {
            fcmService.sendMessageToToken(request);
            return ResponseEntity.ok("Notification has been sent successfully");
        } catch (FirebaseMessagingException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/topic")
    public ResponseEntity<String> sendNotificationToTopic(@RequestBody NotificationDTO request) {
        try {
            fcmService.sendMessageToTopic(request);
            return ResponseEntity.ok("Notification has been sent successfully to topic");
        } catch (FirebaseMessagingException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_EMPLOYER') || hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<List<NotificationResponse>> getUserNotifications(@PathVariable Integer userId) {
        List<Notification> notifications = fcmService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications.stream().map(NotificationResponse::fromNotification).toList());
    }
    @PutMapping("/{notificationId}/read")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_EMPLOYER') || hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<?> markNotificationAsRead(@PathVariable Integer notificationId) {
        try {
            fcmService.markNotificationAsRead(notificationId);
            return ResponseEntity.ok().body("Notification marked as read");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/user/{userId}/read-all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_EMPLOYER') || hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<?> markAllNotificationsAsRead(@PathVariable Integer userId) {
        try {
            fcmService.markAllNotificationsAsRead(userId);
            return ResponseEntity.ok().body("All notifications marked as read");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
