package com.pn.career.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pn.career.models.Application;
import com.pn.career.models.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ApplicationResponse {
    private Integer applicationId;
    private Integer companyId;
    private Integer jobId;
    private String jobTitle;
    private String profileImage;
    private String companyLogo;
    private String lastName;
    private String firstName;
    private Integer year;
    private String email;
    private String companyName;
    private String resumeFile;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    private ApplicationStatus applicationStatus;
    public static ApplicationResponse fromApplication(Application application) {
        return ApplicationResponse.builder()
                .applicationId(application.getApplicationId())
                .companyId(application.getJob().getEmployer().getUserId())
                .jobId(application.getJob().getJobId())
                .profileImage(application.getResume().getStudent().getProfileImage())
                .companyLogo(application.getJob().getEmployer().getCompanyLogo())
                .lastName(application.getResume().getStudent().getLastName())
                .firstName(application.getResume().getStudent().getFirstName())
                .year(application.getResume().getStudent() != null && application.getResume().getStudent().getYear() != null ? application.getResume().getStudent().getYear() : 0).email(application.getResume().getStudent().getEmail())
                .jobTitle(application.getJob().getJobTitle())
                .companyName(application.getJob().getEmployer().getCompanyName())
                .resumeFile(application.getResume().getResumeFile())
                .createdAt(application.getCreatedAt())
                .applicationStatus(application.getApplicationStatus())
                .build();
    }
}
