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
public class ConversationDTOCus {
    private Integer recipientId;
    private String recipientName;
    private String recipientAvatar;
    private String recipientAddress;
    private Integer senderId;
    private String senderName;
    private String senderAvatar;
    private String senderAddress;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private Integer messageId;
    private boolean read;
    private boolean lastSenderId;
    private String createdAt;
}
