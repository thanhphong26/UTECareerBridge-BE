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
    @JsonProperty("phone_number")
    private String phoneNumber;
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("gender")
    private boolean gender;
    @JsonProperty("address")
    private String address;
    @JsonProperty("dob")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dob;
    @JsonProperty("role")
    private Role role;
    @JsonProperty("company_name")
    private String companyName;
    @JsonProperty("company_address")
    private String companyAddress;
    @JsonProperty("company_logo")
    private String companyLogo;
    @JsonProperty("company_email")
    private String companyEmail;
    @JsonProperty("company_description")
    private String companyDescription;
    @JsonProperty("company_website")
    private String companyWebsite;
    @JsonProperty("background_image")
    private String backgroundImage;
    @JsonProperty("video_introduction")
    private String videoIntroduction;
    @JsonProperty("company_size")
    private String companySize;
    @JsonProperty("business_certificate")
    private String businessCertificate;
    @JsonProperty("industry")
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
