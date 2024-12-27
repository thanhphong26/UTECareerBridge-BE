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
public class ConservationDTO {
    private Integer partnerId;
    private String partnerName;
    private String partnerRole;
    private LocalDateTime lastMessageTime;
    private String lastMessageContent;
    private Long unreadCount;
}
