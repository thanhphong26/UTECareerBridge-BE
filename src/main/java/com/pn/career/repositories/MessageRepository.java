package com.pn.career.repositories;

import com.pn.career.dtos.ConservationDTO;
import com.pn.career.models.Message;

import com.pn.career.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderAndRecipientOrderBySentAtDesc(User sender, User recipient);

    List<Message> findByRecipientAndIsReadFalse(User recipient);

    @Query("SELECT m FROM Message m WHERE (m.sender = ?1 AND m.recipient = ?2) OR (m.sender = ?2 AND m.recipient = ?1) ORDER BY m.sentAt")
    List<Message> findConversation(User user1, User user2);

    @Query("SELECT DISTINCT m.sender FROM Message m WHERE m.recipient = ?1 UNION SELECT DISTINCT m.recipient FROM Message m WHERE m.sender = ?1")
    List<User> findContactsByUser(User user);
    List<Message> findUnreadMessagesByRecipient(User recipient);
}
