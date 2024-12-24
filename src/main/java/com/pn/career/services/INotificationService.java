package com.pn.career.services;

public interface INotificationService {
    void sendNotification(Integer userId, String message);
    void sendNotificationToAll(String message);
    void sendNotificationToAllEmployers(String message);
    void sendNotificationToAllCandidates(String message);

}
