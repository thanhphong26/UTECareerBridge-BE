package com.pn.career.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "tokens")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private int tokenId;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String token;
    @Column(name = "token_type")
    private String tokenType;
    @Column(name = "expired_at")
    private String expiredAt;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
