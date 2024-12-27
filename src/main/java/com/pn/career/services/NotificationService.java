package com.pn.career.services;

import com.pn.career.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService implements INotificationService{
    private final NotificationRepository notificationRepository;
    @Override
    public void sendNotification(Integer userId, String message) {

    }

    @Override
    public void sendNotificationToAll(String message) {

    }

    @Override
    public void sendNotificationToAllEmployers(String message) {

    }

    @Override
    public void sendNotificationToAllCandidates(String message) {

    }
}
