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
    private Integer resumeId;
    private Integer jobId;
    private String candidateEmail;
    private String title;
    private String description;
    private String link;
    private LocalDateTime startTime;
    private Integer durationMinutes;
    private List<String> attendeeEmails;
}
