package com.pn.career.responses;

import com.pn.career.models.Notification;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Builder
public class NotificationResponse {
    private int notificationId;
    private int userId;
    private String title;
    private String content;
    private String url;
    private boolean isRead;
    private LocalDateTime notificationDate;
    public static NotificationResponse fromNotification(Notification notification){
        return NotificationResponse.builder()
                .notificationId(notification.getNotificationId())
                .userId(notification.getUser().getUserId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .url(notification.getUrl())
                .isRead(notification.isRead())
                .notificationDate(notification.getNotificationDate())
                .build();
    }
}
