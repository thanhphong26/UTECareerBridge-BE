package com.pn.career.responses;

import com.pn.career.models.JobAlert;
import lombok.*;

import java.util.List;

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
    private List<Integer> level;
    private List<Integer> companyField;
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
                .level(convertStringToList(jobAlert.getLevel()))
                .companyField(convertStringToList(jobAlert.getCompanyField()))
                .jobCategoryId(jobAlert.getJobCategory() != null ? jobAlert.getJobCategory().getJobCategoryId() : null)
                .jobCategoryName(jobAlert.getJobCategory() != null ? jobAlert.getJobCategory().getJobCategoryName() : null)
                .frequency(jobAlert.getFrequency() != null ? jobAlert.getFrequency().name() : null)
                .notifyByEmail(jobAlert.isNotifyByEmail())
                .notifyByApp(jobAlert.isNotifyByApp())
                .active(jobAlert.isActive())
                .build();
    }
    private static List<Integer> convertStringToList(String str) {
        if (str == null || str.isEmpty()) {
            return List.of();
        }
        String[] parts = str.split(",");
        return List.of(parts).stream().map(Integer::parseInt).toList();
    }
}