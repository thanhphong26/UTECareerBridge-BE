package com.pn.career.responses;

import com.pn.career.models.Message;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageResponse {
    private Long id;
    private Integer senderId;
    private String senderName;
    private Integer recipientId;
    private String recipientName;
    private String content;
    private LocalDateTime sentAt;
    private boolean read;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
    public static MessageResponse fromMessage(Message message){
        return MessageResponse.builder()
                .id(message.getId())
                .senderId(message.getSender().getUserId())
                .senderName(message.getSender().getFirstName())
                .recipientId(message.getRecipient().getUserId())
                .recipientName(message.getRecipient().getFirstName())
                .content(message.getContent())
                .sentAt(message.getSentAt())
                .read(message.isRead())
                .readAt(message.getReadAt())
                .createdAt(message.getCreatedAt())
                .build();
    }

}
