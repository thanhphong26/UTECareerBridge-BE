package com.pn.career.responses;

import com.pn.career.models.Notification;
import com.pn.career.models.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class NotificationResponse {
    private int notificationId;
    private Integer userId;
    private String title;
    private String content;
    private NotificationType type;
    private String url;
    private Map<String, Object> data;
    private boolean isRead;
    private LocalDateTime notificationDate;
    public static NotificationResponse fromNotification(Notification notification){
        return NotificationResponse.builder()
                .notificationId(notification.getNotificationId())
                .userId(notification.getUserId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .type(notification.getType())
                .data(notification.getData())
                .url(notification.getUrl())
                .isRead(notification.isRead())
                .notificationDate(notification.getNotificationDate())
                .build();
    }
}
