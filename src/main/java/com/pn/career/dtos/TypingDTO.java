package com.pn.career.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TypingDTO {
    private Integer senderId;
    private Integer recipientId;
    private boolean isTyping;
    private LocalDateTime timestamp;
}
