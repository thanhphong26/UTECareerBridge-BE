package com.pn.career.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "tokens")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long tokenId;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String token;
    @Column(name="refresh_token")
    private String refreshToken;
    @Column(name = "token_type")
    private String tokenType;
    @Column(name="expiration_date")
    private LocalDateTime expirationDate;
    @Column(name="refresh_expiration_date")
    private LocalDateTime refreshExpirationDate;
    @Column(name = "revoked")
    private boolean revoked;
    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;
    @Column(name="expired_at")
    private boolean expired;
    public static String RESET_PASSWORD="RESET_PASSWORD";
    public static String EMAIL_VERIFICATION="EMAIL_VERIFICATION";
}
