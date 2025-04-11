package com.pn.career.repositories;

import com.pn.career.models.Notification;
import com.pn.career.models.NotificationType;
import com.pn.career.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserIdOrderByNotificationDateDesc(Integer userId);
    List<Notification> findByTypeOrderByNotificationDateDesc(String type);
    Page<Notification> findByUserIdAndTypeOrderByNotificationDateDesc(Integer userId, NotificationType type, Pageable pageable);
    Page<Notification> findByUserIdAndReadOrderByNotificationDateDesc(Integer userId, Boolean read, Pageable pageable);
    Page<Notification> findDistinctByTypeAndUserIdOrderByNotificationDateDesc(NotificationType notificationType, Integer userId, Pageable pageable);
    Long countByUserIdAndRead(Integer userId, Boolean read);
    Notification findByUserIdAndNotificationId(Integer userId, Integer notificationId);
}
