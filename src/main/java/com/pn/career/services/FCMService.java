//package com.pn.career.services;
//
//import com.google.firebase.messaging.*;
//import com.pn.career.dtos.NotificationDTO;
//import com.pn.career.models.Notification;
//import com.pn.career.models.User;
//import com.pn.career.repositories.NotificationRepository;
//import com.pn.career.repositories.UserRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class FCMService {
//    private final NotificationRepository notificationRepository;
//    private final UserRepository userRepository;
//    public void subribeToTopic(String token, String topic) throws FirebaseMessagingException {
//        TopicManagementResponse response = FirebaseMessaging.getInstance().subscribeToTopic(List.of(token), topic);
//        log.info("Successfully subscribed to topic: " + response.getSuccessCount() + " tokens");
//    }
//    public void sendMessageToToken(NotificationDTO request) throws FirebaseMessagingException {
//        Message message = Message.builder()
//                .setNotification(com.google.firebase.messaging.Notification.builder()
//                        .setTitle(request.getTitle())
//                        .setBody(request.getBody())
//                        .build())
//                .setToken(request.getToken())
//                .putAllData(request.getData())
//                .build();
//
//        String response = FirebaseMessaging.getInstance().send(message);
//        log.info("Successfully sent message: " + response);
//    }
//
//    public void sendMessageToTopic(NotificationDTO request) throws FirebaseMessagingException {
//        Message message = Message.builder()
//                .setNotification(com.google.firebase.messaging.Notification.builder()
//                        .setTitle(request.getTitle())
//                        .setBody(request.getBody())
//                        .build())
//                .setTopic(request.getTopic())
//                .putAllData(request.getData())
//                .build();
//
//        String response = FirebaseMessaging.getInstance().send(message);
//        log.info("Successfully sent message to topic: " + response);
//    }
//
//    public void saveNotification(User user, String title, String content, String url) {
//        Notification notification = Notification.builder()
//                .user(user)
//                .title(title)
//                .content(content)
//                .url(url)
//                .read(false)
//                .notificationDate(LocalDateTime.now())
//                .build();
//
//        notificationRepository.save(notification);
//    }
//    public void sendNotificationToAdmin(String title, String content, String jobUrl) throws FirebaseMessagingException {
//        Message message = Message.builder()
//                .setNotification(com.google.firebase.messaging.Notification.builder()
//                        .setTitle(title)
//                        .setBody(content)
//                        .build())
//                .setTopic("admin")  // Gửi tới các admin đăng ký với chủ đề này
//                .putData("jobUrl", jobUrl) // Thêm URL vào data của thông báo
//                .build();
//
//        String response = FirebaseMessaging.getInstance().send(message);
//        saveNotification(userRepository.findUserByRole_RoleName("admin"), title, content, jobUrl);
//        log.info("Successfully sent notification to admin: " + response);
//    }
//
//    public List<Notification> getUserNotifications(Integer userId) {
//        User user= userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin user"));
//        return notificationRepository.findAllByUserOrderByNotificationDateDesc(user);
//    }
//
//    // Đánh dấu thông báo đã đọc
//    public void markNotificationAsRead(Integer notificationId) {
//        Notification notification = notificationRepository.findById(notificationId)
//                .orElseThrow(() -> new RuntimeException("Notification not found"));
//        notification.setRead(true);
//        notificationRepository.save(notification);
//    }
//
//    // Đánh dấu tất cả thông báo của user đã đọc
//    public void markAllNotificationsAsRead(Integer userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//        List<Notification> notifications = notificationRepository.findAllByUserAndRead(user, false);
//        notifications.forEach(notification -> notification.setRead(true));
//        notificationRepository.saveAll(notifications);
//    }
//}
