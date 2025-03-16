package com.pn.career.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterviewRequestDTO {
    private String resumeId;
    private String candidateEmail;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private Integer durationMinutes;
    private List<String> attendeeEmails;
}
