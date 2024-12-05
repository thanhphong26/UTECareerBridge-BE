package com.pn.career.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pn.career.dtos.JobSkillDTO;
import com.pn.career.models.Job;
import com.pn.career.models.JobCategory;
import com.pn.career.models.JobLevel;
import com.pn.career.models.JobSkill;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobResponse {
    private Integer jobId;
    private Integer employerId;
    private EmployerResponse employerResponse;
    private JobCategory jobCategory;
    private JobLevel jobLevel;
    private String jobTitle;
    private String jobDescription;
    private String jobRequirements;
    private String jobLocation;
    private BigDecimal jobMinSalary;
    private BigDecimal jobMaxSalary;
    private int amount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate jobDeadline;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDateTime updatedAt;
    private List<JobSkillDTO> jobSkills;
    private String rejectionReason;
    private Integer packageId;
    public static JobResponse fromJob(Job job) {
        return JobResponse.builder()
                .jobId(job.getJobId())
                .employerId(job.getEmployer().getUserId())
                .employerResponse(EmployerResponse.fromUser(job.getEmployer()))
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
                .rejectionReason(job.getRejectionReason())
                .packageId(job.getPackageId())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }
    public static List<JobSkillDTO> convertJobSkillToDTO(List<JobSkill> jobSkills) {
        return jobSkills.stream()
                .map(jobSkill -> JobSkillDTO.builder()
                        .skillId(jobSkill.getSkill().getSkillId())
                        .skillName(jobSkill.getSkill().getSkillName())
                        .build())
                .toList();
    }
}
