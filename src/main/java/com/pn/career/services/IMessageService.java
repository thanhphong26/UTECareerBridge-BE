package com.pn.career.services;

import com.pn.career.dtos.ConservationDTO;
import com.pn.career.dtos.ConversationDTO;
import com.pn.career.models.Message;
import com.pn.career.models.User;
import com.pn.career.responses.MessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IMessageService {
    ConversationDTO sendMessage(Integer senderId, Integer recipientId, String content);
    List<MessageResponse> getConservation(Integer user1Id, Integer user2Id);
    List<User> getContacts(Integer userId);
    void markAsRead(Long messageId);
    List<MessageResponse> getUnreadMessages(Integer userId);
    Page<ConversationDTO> getConversations(Integer userId, PageRequest pageRequest);
}
