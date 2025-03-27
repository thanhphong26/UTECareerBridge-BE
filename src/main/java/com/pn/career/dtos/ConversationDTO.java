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
public class ConversationDTO {
    private Integer recipientId;
    private String name;
    private String avatar;
    private String address;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private Integer messageId;
    private boolean read;
    private boolean lastSenderId;
    private String createdAt;
    // Constructor matching the query
    public ConversationDTO(Integer recipientId, String name, String avatar, String address,
                           String lastMessage, Integer messageId, LocalDateTime lastMessageAt, boolean read,
                           int lastSenderId, String createdAt) {
        this.recipientId = recipientId;
        this.name = name;
        this.avatar = avatar;
        this.address = address;
        this.lastMessage = lastMessage;
        this.messageId = messageId;
        this.lastMessageAt = lastMessageAt;
        this.read = read;
        this.lastSenderId = lastSenderId == 1;
        this.createdAt = createdAt;
    }
}
