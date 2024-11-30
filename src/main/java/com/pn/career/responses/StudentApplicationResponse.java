package com.pn.career.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pn.career.models.Resume;
import com.pn.career.models.Skill;
import com.pn.career.models.StudentSkill;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.IntToDoubleFunction;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentApplicationResponse {
    private Integer studentId;
    private Integer resumeId;
    private String lastName;
    private String firstName;
    private String profileImage;
    private boolean gender;
    private String email;
    private String universityEmail;
    private String phoneNumber;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private String dob;
    private Integer year;
    private Integer provinceId;
    private Integer districtId;
    private Integer wardId;
    private String address;
    private String resumeFile;
    private List<StudentSkillResponse> studentSkills;
    private String categoryName;
    private String levelName;
    public static StudentApplicationResponse fromStudent(Resume resume) {
        return StudentApplicationResponse.builder()
                .studentId(resume.getStudent().getUserId())
                .resumeId(resume.getResumeId())
                .lastName(resume.getStudent().getLastName())
                .firstName(resume.getStudent().getFirstName())
                .profileImage(resume.getStudent().getProfileImage())
                .gender(resume.getStudent().isGender())
                .email(resume.getStudent().getEmail())
                .universityEmail(resume.getStudent().getUniversityEmail())
                .phoneNumber(resume.getStudent().getPhoneNumber())
                .dob(DateTimeFormatter.ofPattern("dd/MM/yyyy").format(resume.getStudent().getDob()))
                .year(resume.getStudent().getYear())
                .provinceId(resume.getStudent().getProvinceId())
                .districtId(resume.getStudent().getDistrictId())
                .wardId(resume.getStudent().getWardId())
                .address(resume.getStudent().getAddress())
                .resumeFile(resume.getResumeFile())
                .studentSkills(resume.getStudent().getStudentSkills().stream().map(StudentSkillResponse::fromStudentSkill).toList())
                .categoryName(resume.getStudent().getJobCategory().getJobCategoryName())
                .levelName(resume.getJobLevel().getNameLevel())
                .build();
    }
}
