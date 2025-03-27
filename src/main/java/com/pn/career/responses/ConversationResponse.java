package com.pn.career.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pn.career.models.Employer;
import com.pn.career.models.Student;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConversationResponse {
    private Integer recipientId;
    private String name;
    private String avatar;
    private String address;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private boolean read;
    private boolean lastSenderId;
    @JsonFormat(pattern = "HH:mm")
    private LocalDateTime createdAt;
   public static ConversationResponse fromStudent(Student student){
        return ConversationResponse.builder()
                .recipientId(student.getUserId())
                .name(student.getFirstName()+" "+student.getLastName())
                .avatar(student.getProfileImage())
                .address(student.getAddress())
                .build();

    }
    public static ConversationResponse fromEmployer(Employer employer){
        return ConversationResponse.builder()
                .recipientId(employer.getUserId())
                .name(employer.getCompanyName())
                .avatar(employer.getCompanyLogo())
                .address(employer.getAddress())
                .build();
    }
}
