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
    private Integer employerId;
    private String employerName;
    private String companyLogo;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
}
