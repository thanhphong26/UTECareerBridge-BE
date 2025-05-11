package com.pn.career.services;

import com.pn.career.models.Notification;
import com.pn.career.models.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Map;

public interface INotificationService {
    Notification getById(Integer notificationId, Integer userId);
    void sendRoleNotification(String role, String title, String message);
    void sendBroadcastNotification(String title, String message);
    void sendPersonalNotification(Integer userId, String title, String message, Map<String, Object> data);
    void sendNotificationForJobApplicationForStudent(Integer userId, String jobTitle, String message, Map<String, Object> data);
    void sendNotificationForJobApplicationForEmployer(String employerId, String studentId, String studentName, String jobTitle);
    void sendApplicationStatusNotification(String studentId, String jobTitle, String companyName, String status);
    void sendNotificationToUser(Integer userId, String title, String message);
    Page<Notification> getUserNotifications(Integer userID, PageRequest pageable);
    Page<Notification> getUnreadNotifications(Integer userId, PageRequest pageable);
    Page<Notification> getBroadcastNotifications(Integer userId, PageRequest pageable);
    void makeNotificationAsRead(Integer notificationId);
    void makeAllNotificationsAsRead(Integer userId);
    Long getUnreadNotificationCount(Integer userId);
    void deleteNotification(Integer notificationId);
    void deleteAllNotifications(Integer userId);
    void sendEventNotification( String eventTitle, String eventDescription, NotificationType notificationType, LocalDateTime eventDate, String eventLocation, String url);
    void sendNotificationApprovedJob(String title, String content, String url);
    void sendNotificationRejectedApplication(Integer studentId, String title, String content);
    void sendNotificationInterview(String title, String content, String url, Integer studentId, String jobTitle, String companyName);
    Page<Notification> getUserPersonalNotifications(Integer userID, PageRequest pageable);
    void sendNotificationForJobAlert(String title, String message, Integer userId, Map<String, Object> data);
    void sendNotificationForStudentFollowEmployer(String title, String message, Integer userId);

}
