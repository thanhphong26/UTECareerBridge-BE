package com.pn.career.services;

import com.pn.career.dtos.NotificationDTO;
import com.pn.career.models.Notification;
import com.pn.career.models.NotificationType;
import com.pn.career.models.Role;
import com.pn.career.models.User;
import com.pn.career.repositories.NotificationRepository;
import com.pn.career.repositories.RoleRepository;
import com.pn.career.repositories.UserRepository;
import com.pn.career.responses.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService implements INotificationService{
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final RoleRepository  roleRepository;
    @Override
    public void sendRoleNotification(String role, String title, String message) {
        Notification notification = Notification.builder()
                .title(title)
                .content(message)
                .notificationDate(LocalDateTime.now())
                .type(NotificationType.ROLE)
                .build();
        //save data to database
        notificationRepository.save(notification);
        messagingTemplate.convertAndSend("/notifications/" + role.toLowerCase(), notification);
        Optional<Role> roleOptional = roleRepository.findByRoleName(role);
        List<User> users = userRepository.findByRole(roleOptional.get());
        users.stream().forEach(user -> {
            Notification notificationUser = Notification.builder()
                    .userId(user.getUserId())
                    .title(title)
                    .content(message)
                    .notificationDate(LocalDateTime.now())
                    .type(NotificationType.ROLE)
                    .read(false)
                    .build();
            notificationRepository.save(notificationUser);
        });
    }

    @Override
    public void sendBroadcastNotification(String title, String message) {
        Notification notification = Notification.builder()
                .title(title)
                .content(message)
                .notificationDate(LocalDateTime.now())
                .type(NotificationType.BROADCAST)
                .build();
        notificationRepository.save(notification);
        messagingTemplate.convertAndSend("/notifications/broadcast", notification);
        List<User> users = userRepository.findAllByActiveExceptAdmin();
        users.stream().forEach(user -> {
            Notification notificationUser = Notification.builder()
                    .userId(user.getUserId())
                    .title(title)
                    .content(message)
                    .notificationDate(LocalDateTime.now())
                    .type(NotificationType.BROADCAST)
                    .read(false)
                    .build();
            notificationRepository.save(notificationUser);
        });
    }

    @Override
    public void sendPersonalNotification(Integer userId, String title, String message, Map<String, Object> data) {
        Notification payload = Notification.builder()
                .title(title)
                .content(message)
                .notificationDate(LocalDateTime.now())
                .type(NotificationType.PERSONAL)
                .data(data)
                .build();
        messagingTemplate.convertAndSendToUser(String.valueOf(userId), "/notifications/personal", payload);

    }

    @Override
    public void sendNotificationForJobApplicationForStudent(Integer userId, String jobTitle, String message, Map<String, Object> data) {
       //
    }

    @Override
    public void sendNotificationForJobApplicationForEmployer(String employerId, String studentId, String studentName, String jobTitle) {
        Map<String, Object> data = new HashMap<>();
        data.put("studentId", studentId);
        data.put("jobTitle", jobTitle);

        Notification payload = Notification.builder()
                .title("New Job Application")
                .userId(Integer.parseInt(employerId))
                .content(studentName + " has applied for the position: " + jobTitle)
                .notificationDate(LocalDateTime.now())
                .type(NotificationType.JOB_APPLICATION_EMPLOYER)
                .data(data)
                .build();
        notificationRepository.save(payload);
        messagingTemplate.convertAndSendToUser(employerId, "/notifications/applications", payload);
    }

    @Override
    public void sendApplicationStatusNotification(String studentId, String jobTitle, String companyName, String status) {
        Map<String, Object> data = new HashMap<>();
        data.put("jobTitle", jobTitle);
        data.put("companyName", companyName);
        data.put("status", status);

        Notification payload = Notification.builder()
                .title("Application Status Update")
                .content("Your application for " + jobTitle + " at " + companyName + " has been updated to: " + status)
                .notificationDate(LocalDateTime.now())
                .type(NotificationType.APPLICATION_STATUS)
                .data(data)
                .build();

        messagingTemplate.convertAndSendToUser(studentId, "/notifications/status", payload);
    }

    @Override
    public void sendNotificationToUser(Integer userId, String title, String message) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Notification notification = Notification.builder()
                .userId(user.getUserId())
                .title(title)
                .content(message)
                .notificationDate(LocalDateTime.now())
                .read(false)
                .type(NotificationType.PERSONAL)
                .build();
        notificationRepository.save(notification);
        messagingTemplate.convertAndSendToUser(String.valueOf(userId), "/notifications/personal", notification);
    }

    @Override
    public Page<Notification> getUserNotifications(Integer userID, PageRequest pageable) {
        User user = userRepository.findById(userID).orElseThrow(() -> new RuntimeException("User not found"));
        Page<Notification> notifications = notificationRepository.findByUserIdAndTypeOrderByNotificationDateDesc(user.getUserId(), NotificationType.PERSONAL, pageable);
        return notifications;
    }

    @Override
    public Page<Notification> getUnreadNotifications(Integer userId, PageRequest pageable) {
        return notificationRepository.findByUserIdAndReadOrderByNotificationDateDesc(userId, false, pageable);
    }

    @Override
    public Page<Notification> getBroadcastNotifications(PageRequest pageable) {
        return notificationRepository.findByTypeOrderByNotificationDateDesc(NotificationType.BROADCAST, pageable);
    }

    @Override
    public void makeNotificationAsRead(Integer notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public void makeAllNotificationsAsRead(Integer userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByNotificationDateDesc(userId);
        notifications.stream().forEach(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }

    @Override
    public Long getUnreadNotificationCount(Integer userId) {
        return notificationRepository.countByUserIdAndRead(userId, false);
    }

    @Override
    public void deleteNotification(Integer notificationId) {
        notificationRepository.deleteById(notificationId);
    }
    @Override
    public void deleteAllNotifications(Integer userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByNotificationDateDesc(userId);
        notifications.stream().forEach(notification -> {
            notificationRepository.delete(notification);
        });
    }
    @Override
    public void sendEventNotification(String eventTitle, String eventDescription, NotificationType notificationType, LocalDateTime eventDate, String eventLocation, String url) {
        Notification notification = Notification.builder()
                .title(eventTitle)
                .content(eventDescription)
                .notificationDate(LocalDateTime.now())
                .type(notificationType)
                .data(Map.of("eventDate", eventDate, "eventLocation", eventLocation))
                .url(url)
                .build();
        notificationRepository.save(notification);
        messagingTemplate.convertAndSend("/notifications/broadcast", notification);

        List<User> users = userRepository.findAllByActiveExceptAdmin();
        users.stream().forEach(user -> {
            Notification notificationUser = Notification.builder()
                    .userId(user.getUserId())
                    .title(eventTitle)
                    .content(eventDescription)
                    .notificationDate(LocalDateTime.now())
                    .type(notificationType)
                    .read(false)
                    .data(Map.of("eventDate", eventDate, "eventLocation", eventLocation))
                    .url(url)
                    .build();
            notificationRepository.save(notificationUser);
        });
    }

    @Override
    public void sendNotificationApprovedJob( String title, String content, String url) {
        User admin = userRepository.findByRole(roleRepository.findByRoleName("ADMIN").get()).get(0);
        Notification notification = Notification.builder()
                .userId(admin.getUserId())
                .title(title)
                .content(content)
                .notificationDate(LocalDateTime.now())
                .type(NotificationType.ADMIN)
                .url(url)
                .build();
        notificationRepository.save(notification);
        messagingTemplate.convertAndSendToUser(String.valueOf(admin.getUserId()), "/notifications/role", notification);
    }
}
