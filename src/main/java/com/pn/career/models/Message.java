package com.pn.career.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages", indexes = {
        @Index(name = "idx_sender_id", columnList = "sender_id"),
        @Index(name = "idx_recipient_id", columnList = "recipient_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Message extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;
    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private User recipient;
    @Column(nullable = false)
    private String content;
    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;
    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;
    @Column(name = "read_at")
    private LocalDateTime readAt;
}
