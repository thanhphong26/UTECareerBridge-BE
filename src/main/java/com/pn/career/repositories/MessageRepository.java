package com.pn.career.repositories;

import com.pn.career.dtos.ConversationDTO;
import com.pn.career.models.Message;

import com.pn.career.models.User;
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

    @Query(value = "SELECT new com.pn.career.dtos.ConversationDTO(" +
            "CASE WHEN m.sender.userId = :userId THEN m.recipient.userId ELSE m.sender.userId END, " +  // recipientId
            "CASE WHEN m.sender.userId = :userId THEN m.recipient.firstName ELSE m.sender.firstName END, " +  // name
            "CASE WHEN m.sender.userId = :userId THEN " +
            "   (CASE " +
            "      WHEN EXISTS (SELECT 1 FROM Student s WHERE s.userId = m.recipient.userId) THEN " +
            "         (SELECT s.profileImage FROM Student s WHERE s.userId = m.recipient.userId) " +
            "      WHEN EXISTS (SELECT 1 FROM Employer e WHERE e.userId = m.recipient.userId) THEN " +
            "         (SELECT e.companyLogo FROM Employer e WHERE e.userId = m.recipient.userId) " +
            "      ELSE NULL " +
            "    END) " +
            "ELSE " +
            "   (CASE " +
            "      WHEN EXISTS (SELECT 1 FROM Student s WHERE s.userId = m.sender.userId) THEN " +
            "         (SELECT s.profileImage FROM Student s WHERE s.userId = m.sender.userId) " +
            "      WHEN EXISTS (SELECT 1 FROM Employer e WHERE e.userId = m.sender.userId) THEN " +
            "         (SELECT e.companyLogo FROM Employer e WHERE e.userId = m.sender.userId) " +
            "      ELSE NULL " +
            "    END) " +
            "END, " +  // avatar - checking role and getting the appropriate image field
            "CASE WHEN m.sender.userId = :userId THEN m.recipient.address ELSE m.sender.address END, " +  // address
            "m.content, " +  // lastMessage
            "m.sentAt, " +  // lastMessageAt
            "m.isRead, " +  // read status
            "CASE WHEN m.sender.userId = :userId THEN 1 ELSE 0 END, " +  // lastSenderId (1 if current user is last sender, 0 otherwise)
            "m.createdAt) " +  // createdAt
            "FROM Message m " +
            "WHERE m.id IN (" +
            "   SELECT MAX(m2.id) FROM Message m2 " +
            "   WHERE (m2.sender.userId = :userId AND m2.recipient.userId = CASE WHEN m.sender.userId = :userId THEN m.recipient.userId ELSE m.sender.userId END) " +
            "   OR (m2.recipient.userId = :userId AND m2.sender.userId = CASE WHEN m.sender.userId = :userId THEN m.recipient.userId ELSE m.sender.userId END) " +
            "   GROUP BY CASE WHEN m2.sender.userId = :userId THEN m2.recipient.userId ELSE m2.sender.userId END" +
            ") " +
            "ORDER BY m.sentAt DESC")
    List<ConversationDTO> findLatestConversationsForUser(@Param("userId") Integer userId);
}
