package com.pn.career.repositories;

import com.pn.career.models.Notification;
import com.pn.career.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findAllByUserOrderByNotificationDateDesc(User user);
    List<Notification> findAllByUserAndRead(User user, boolean isRead);
}
