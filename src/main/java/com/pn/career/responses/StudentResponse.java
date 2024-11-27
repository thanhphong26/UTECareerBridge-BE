package com.pn.career.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pn.career.models.Role;
import com.pn.career.models.Student;
import com.pn.career.models.User;
import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentResponse {
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
    private int categoryId;
    private int year;
    private Role role;
    public static StudentResponse fromUser(User user) {
        return StudentResponse.builder()
                .id(user.getUserId())
                .phoneNumber(user.getPhoneNumber())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .gender(user.isGender())
                .dob(user.getDob())
                .provinceId(user.getProvinceId())
                .districtId(user.getDistrictId())
                .wardId(user.getWardId())
                .address(user.getAddress())
                .role(user.getRole())
                .build();
    }
    public static StudentResponse fromStudent(Student student) {
        return StudentResponse.builder()
                .id(student.getUserId())
                .email(student.getEmail())
                .phoneNumber(student.getPhoneNumber())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .universityEmail(student.getUniversityEmail())
                .gender(student.isGender())
                .dob(student.getDob())
                .provinceId(student.getProvinceId())
                .districtId(student.getDistrictId())
                .wardId(student.getWardId())
                .address(student.getAddress())
                .categoryId(student.getCategoryId())
                .profileImage(student.getProfileImage())
                .year(student.getYear())
                .role(student.getRole())
                .build();
    }
}
