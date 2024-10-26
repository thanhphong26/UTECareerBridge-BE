package com.pn.career.dtos;

import com.pn.career.models.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {
    private Long id;
    private Long senderId;
    private String senderName;
    private Long recipientId;
    private String recipientName;
    private String content;
    private LocalDateTime sentAt;
    private boolean read;
    private LocalDateTime readAt;
    public static MessageDTO from(Message message) {
        return MessageDTO.builder()
                .id(message.getId())
                .senderId((long) message.getSender().getUserId())
                .senderName(message.getSender().getFirstName())
                .recipientId((long) message.getRecipient().getUserId())
                .recipientName(message.getRecipient().getFirstName())
                .content(message.getContent())
                .sentAt(message.getSentAt())
                .read(message.isRead())
                .readAt(message.getReadAt())
                .build();
    }
}
