package com.pn.career.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pn.career.models.Resume;
import com.pn.career.models.Student;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentViewResponse {
    private int id;
    private String email;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private boolean gender;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dob;
    private int provinceId;
    private int districtId;
    private int wardId;
    private String address;
    private String profileImage;
    private String universityEmail;
    private int year;
    private String resumeFile;
    private List<StudentSkillResponse> studentSkills;
    private String categoryName;
    private String levelName;
    public static StudentViewResponse fromStudent(Student student) {
        return StudentViewResponse.builder()
                .id(student.getUserId())
                .email(student.getEmail())
                .phoneNumber(student.getPhoneNumber())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .gender(student.isGender())
                .dob(student.getDob())
                .provinceId(student.getProvinceId())
                .districtId(student.getDistrictId())
                .wardId(student.getWardId())
                .address(student.getAddress())
                .profileImage(student.getProfileImage())
                .universityEmail(student.getUniversityEmail())
                .year(student.getYear())
                .resumeFile(student.getResumes().stream().filter(Resume::isActive).findFirst().get().getResumeFile())
                .studentSkills(student.getStudentSkills().stream().map(StudentSkillResponse::fromStudentSkill).toList())
                .categoryName(student.getJobCategory().getJobCategoryName())
                .levelName(student.getResumes().stream().filter(Resume::isActive).findFirst().get().getJobLevel().getNameLevel())
                .build();
    }
}
