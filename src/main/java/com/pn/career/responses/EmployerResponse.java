package com.pn.career.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pn.career.models.Employer;
import com.pn.career.models.Industry;
import com.pn.career.models.Role;
import com.pn.career.models.User;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployerResponse {
    @JsonProperty("id")
    private int id;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private boolean gender;
    private String address;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dob;
    private Role role;
    private String companyName;
    private String companyAddress;
    private String companyLogo;
    private String companyEmail;
    private String companyDescription;
    private String companyWebsite;
    private String backgroundImage;
    private String videoIntroduction;
    private String companySize;
    private String businessCertificate;
    private Industry industry;
    public static EmployerResponse fromUser(Employer employer) {
        return EmployerResponse.builder()
                .id(employer.getUserId())
                .phoneNumber(employer.getPhoneNumber())
                .firstName(employer.getFirstName())
                .lastName(employer.getLastName())
                .gender(employer.isGender())
                .dob(employer.getDob())
                .role(employer.getRole())
                .companyAddress(employer.getCompanyAddress())
                .companyName(employer.getCompanyName())
                .companyEmail(employer.getCompanyEmail())
                .companyLogo(employer.getCompanyLogo())
                .companyDescription(employer.getCompanyDescription())
                .companyWebsite(employer.getCompanyWebsite())
                .backgroundImage(employer.getBackgroundImage())
                .videoIntroduction(employer.getVideoIntroduction())
                .companySize(employer.getCompanySize())
                .businessCertificate(employer.getBusinessCertificate())
                .industry(employer.getIndustry())
                .build();
    }
    public static EmployerResponse fromUser(User user) {
        return EmployerResponse.builder()
                .id(user.getUserId())
                .phoneNumber(user.getPhoneNumber())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .address(user.getAddress())
                .build();
    }
}
