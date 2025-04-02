package com.pn.career.models;

import com.pn.career.converters.JpaConverterJson;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Table(name = "notifications")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Integer notificationId;
    @Column(name = "user_id")
    private Integer userId;
    @Column(name = "title")
    private String title;
    private String content;
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    private String url;
    @Convert(converter = JpaConverterJson.class)
    private Map<String, Object> data;
    @Column(name = "is_read")
    private boolean read;
    @Column(name = "notification_date")
    private LocalDateTime notificationDate;
}
