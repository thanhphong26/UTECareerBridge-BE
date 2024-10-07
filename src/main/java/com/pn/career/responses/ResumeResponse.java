package com.pn.career.responses;

import com.pn.career.models.Resume;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String levelName;
    public static ResumeResponse fromResume(Resume resume) {
        return ResumeResponse.builder()
                .resumeId(resume.getResumeId())
                .studentResponse(StudentResponse.fromUser(resume.getStudent()))
                .resumeTitle(resume.getResumeTitle())
                .resumeFile(resume.getResumeFile())
                .resumeDescription(resume.getResumeDescription())
                .levelId(resume.getJobLevel().getJobLevelId())
                .levelName(resume.getJobLevel().getNameLevel())
                .build();
    }

}
