package com.pn.career.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pn.career.models.Application;
import com.pn.career.models.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ApplicationResponse {
    private Integer applicationId;
    private ResumeResponse resumeResponse;
    private JobResponse jobResponse;
    private ApplicationStatus applicationStatus;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    public static ApplicationResponse fromApplication(Application application) {
        return ApplicationResponse.builder()
                .applicationId(application.getApplicationId())
                .resumeResponse(ResumeResponse.fromResume(application.getResume()))
                .jobResponse(JobResponse.fromJob(application.getJob()))
                .applicationStatus(application.getApplicationStatus())
                .createdAt(application.getCreatedAt())
                .build();
    }
}
