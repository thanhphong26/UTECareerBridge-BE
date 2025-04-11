package com.pn.career.models;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("chatHistory")
@Getter
@Setter
public class ChatHistory implements Serializable {
    @Id
    private String id;

    @Indexed
    private String sessionId;

    @Indexed
    private String userId;

    private String userType;
    private String content;
    private boolean isUserMessage;
    private LocalDateTime timestamp;
}
