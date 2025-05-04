package com.pn.career.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pn.career.models.Resume;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResumeResponse {
    private Integer resumeId;
    private StudentResponse studentResponse;
    private String resumeTitle;
    private String resumeFile;
    private String resumeDescription;
    private Integer levelId;
    private Boolean isActive;
    private String levelName;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime updatedAt;
    private Object theme;
    private Object personalInfo;
    private Object sections;
    private Object workExperience;
    private Object certificates;
    public static ResumeResponse fromResume(Resume resume) {
        return ResumeResponse.builder()
                .resumeId(resume.getResumeId())
                .studentResponse(StudentResponse.fromStudent(resume.getStudent()))
                .resumeTitle(resume.getResumeTitle())
                .resumeFile(resume.getResumeFile())
                .resumeDescription(resume.getResumeDescription())
                .levelId(resume.getJobLevel().getJobLevelId())
                .levelName(resume.getJobLevel().getNameLevel())
                .createdAt(resume.getCreatedAt())
                .updatedAt(resume.getUpdatedAt())
                .isActive(resume.isActive())
                .theme(resume.getTheme())
                .personalInfo(resume.getPersonalInfo())
                .sections(resume.getSections())
                .workExperience(resume.getWorkExperience())
                .certificates(resume.getCertificates())
                .build();
    }
}
