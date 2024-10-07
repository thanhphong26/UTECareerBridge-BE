package com.pn.career.responses;

import com.pn.career.models.Application;
import com.pn.career.models.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ApplicationResponse {
    private Integer applicationId;
    private ResumeResponse resumeResponse;
    private JobResponse jobResponse;
    private ApplicationStatus applicationStatus;
    public static ApplicationResponse fromApplication(Application application) {
        return ApplicationResponse.builder()
                .applicationId(application.getApplicationId())
                .resumeResponse(ResumeResponse.fromResume(application.getResume()))
                .jobResponse(JobResponse.fromJob(application.getJob()))
                .applicationStatus(application.getApplicationStatus())
                .build();
    }
}
