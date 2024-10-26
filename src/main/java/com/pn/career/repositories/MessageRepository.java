package com.pn.career.repositories;

import com.pn.career.dtos.ConservationDTO;
import com.pn.career.models.Message;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("SELECT new com.pn.career.dtos.ConservationDTO(" +
            "CASE WHEN m.sender.userId = :userId THEN m.recipient.userId ELSE m.sender.userId END, " +
            "CASE WHEN m.sender.userId = :userId THEN m.recipient.firstName ELSE m.sender.firstName END, " +
            "CASE WHEN m.sender.userId = :userId THEN m.recipient.role.roleName ELSE m.sender.role.roleName END, " +
            "MAX(m.sentAt), " +
            "(SELECT mm.content FROM Message mm WHERE mm.id = (" +
            "    SELECT MAX(mmm.id) FROM Message mmm " +
            "    WHERE (mmm.sender.userId = :userId AND mmm.recipient.userId = " +
            "        CASE WHEN m.sender.userId = :userId THEN m.recipient.userId ELSE m.sender.userId END) " +
            "    OR (mmm.sender.userId = CASE WHEN m.sender.userId = :userId THEN m.recipient.userId ELSE m.sender.userId END " +
            "        AND mmm.recipient.userId = :userId)" +
            ")), " +
            "SUM(CASE WHEN m.recipient.userId = :userId AND m.isRead = false THEN 1 ELSE 0 END)) " +
            "FROM Message m " +
            "WHERE m.sender.userId = :userId OR m.recipient.userId = :userId " +
            "GROUP BY CASE WHEN m.sender.userId = :userId THEN m.recipient.userId ELSE m.sender.userId END, " +
            "         CASE WHEN m.sender.userId = :userId THEN m.recipient.firstName ELSE m.sender.firstName END, " +
            "         CASE WHEN m.sender.userId = :userId THEN m.recipient.role.roleName ELSE m.sender.role.roleName END " +
            "ORDER BY MAX(m.sentAt) DESC")
    List<ConservationDTO> findConversationsForUser(@Param("userId") Integer userId);


    @Query("SELECT m FROM Message m " +
            "WHERE (m.sender.userId = :currentUserId AND m.recipient.userId = :partnerId) " +
            "OR (m.sender.userId = :partnerId AND m.recipient.userId = :currentUserId) " +
            "ORDER BY m.sentAt DESC")
    Page<Message> findMessagesBetweenUsers(@Param("currentUserId") Integer currentUserId,
                                           @Param("partnerId") Integer partnerId,
                                           Pageable pageable);
}
