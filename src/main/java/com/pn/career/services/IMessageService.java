package com.pn.career.services;

import com.pn.career.models.Message;
import com.pn.career.models.User;
import com.pn.career.responses.MessageResponse;

import java.util.List;

public interface IMessageService {
    MessageResponse sendMessage(Integer senderId, Integer recipientId, String content);
    List<MessageResponse> getConservation(Integer user1Id, Integer user2Id);
    List<User> getContacts(Integer userId);
    void markAsRead(Long messageId);
    List<MessageResponse> getUnreadMessages(Integer userId);
}
