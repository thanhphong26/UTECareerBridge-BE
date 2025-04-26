package com.pn.career.responses;

import com.pn.career.models.JobAlert;
import lombok.*;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobAlertResponse {
    private Long id;
    private String jobTitle;
    private Double minSalary;
    private String location;
    private Integer jobCategoryId;
    private String level;
    private String companyField;
    private String frequency;
    private boolean notifyByEmail;
    private boolean notifyByApp;
    private boolean active;
    private String jobCategoryName;
    public static JobAlertResponse from(JobAlert jobAlert) {
        return JobAlertResponse.builder()
                .id(jobAlert.getId())
                .jobTitle(jobAlert.getJobTitle())
                .minSalary(jobAlert.getMinSalary())
                .location(jobAlert.getLocation())
                .level(jobAlert.getLevel())
                .companyField(jobAlert.getCompanyField())
                .jobCategoryId(jobAlert.getJobCategory() != null ? jobAlert.getJobCategory().getJobCategoryId() : null)
                .jobCategoryName(jobAlert.getJobCategory() != null ? jobAlert.getJobCategory().getJobCategoryName() : null)
                .frequency(jobAlert.getFrequency() != null ? jobAlert.getFrequency().name() : null)
                .notifyByEmail(jobAlert.isNotifyByEmail())
                .notifyByApp(jobAlert.isNotifyByApp())
                .active(jobAlert.isActive())
                .build();
    }
}
