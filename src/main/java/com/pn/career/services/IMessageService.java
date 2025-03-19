package com.pn.career.services;

import com.pn.career.dtos.ConversationDTO;
import com.pn.career.dtos.ConversationDTOCus;
import com.pn.career.models.User;
import com.pn.career.responses.MessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IMessageService {
    ConversationDTOCus sendMessage(Integer senderId, Integer recipientId, String content);
    Page<MessageResponse> getConservation(Integer user1Id, Integer user2Id, PageRequest pageRequest);
    List<User> getContacts(Integer userId);
    void markAsRead(Long messageId);
    List<MessageResponse> getUnreadMessages(Integer userId);
    Page<ConversationDTO> getConversations(Integer userId, PageRequest pageRequest);
}
