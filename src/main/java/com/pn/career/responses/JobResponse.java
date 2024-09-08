package com.pn.career.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pn.career.models.Job;
import com.pn.career.models.JobCategory;
import com.pn.career.models.JobLevel;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobResponse {
    @JsonProperty("job_id")
    private Integer jobId;
    @JsonProperty("employer_id")
    private Integer employerId;
    @JsonProperty("category")
    private JobCategory jobCategory;
    @JsonProperty("level")
    private JobLevel jobLevel;
    @JsonProperty("job_title")
    private String jobTitle;
    @JsonProperty("job_description")
    private String jobDescription;
    @JsonProperty("job_requirements")
    private String jobRequirements;
    @JsonProperty("job_location")
    private String jobLocation;
    @JsonProperty("job_min_salary")
    private BigDecimal jobMinSalary;
    @JsonProperty("job_max_salary")
    private BigDecimal jobMaxSalary;
    @JsonProperty("amount")
    private int amount;
    @JsonProperty("job_deadline")
    private LocalDate jobDeadline;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    public static JobResponse fromJob(Job job) {
        return JobResponse.builder()
                .jobId(job.getJobId())
                .employerId(job.getEmployer().getUserId())
                .jobCategory(JobCategory.builder()
                        .jobCategoryId(job.getJobCategory().getJobCategoryId())
                        .jobCategoryName(job.getJobCategory().getJobCategoryName())
                        .isActive(job.getJobCategory().isActive())
                        .createdAt(job.getJobCategory().getCreatedAt())
                        .updatedAt(job.getJobCategory().getUpdatedAt())
                        .build())
                .jobLevel(JobLevel.builder()
                        .jobLevelId(job.getJobLevel().getJobLevelId())
                        .nameLevel(job.getJobLevel().getNameLevel())
                        .isActive(job.getJobLevel().isActive())
                        .createdAt(job.getJobLevel().getCreatedAt())
                        .updatedAt(job.getJobLevel().getUpdatedAt())
                        .build())
                .jobTitle(job.getJobTitle())
                .jobDescription(job.getJobDescription())
                .jobRequirements(job.getJobRequirements())
                .jobLocation(job.getJobLocation())
                .jobMinSalary(job.getJobMinSalary())
                .jobMaxSalary(job.getJobMaxSalary())
                .amount(job.getAmount())
                .jobDeadline(job.getJobDeadline())
                .createdAt(job.getCreatedAt())
                .build();
    }
}
