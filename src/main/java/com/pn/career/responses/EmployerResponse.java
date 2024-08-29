package com.pn.career.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pn.career.models.Employer;
import com.pn.career.models.Industry;
import com.pn.career.models.Role;
import com.pn.career.models.User;
import lombok.*;

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
    @JsonProperty("address")
    private String address;
    @JsonProperty("role")
    private Role role;
    @JsonProperty("company_name")
    private String companyName;
    @JsonProperty("company_address")
    private String companyAddress;
    @JsonProperty("company_logo")
    private String companyLogo;
    @JsonProperty("company_description")
    private String companyDescription;
    @JsonProperty("company_website")
    private String companyWebsite;
    @JsonProperty("background_image")
    private String backgroundImage;
    @JsonProperty("video_introduction")
    private String videoIntroduction;
    @JsonProperty("company_size")
    private int companySize;
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
                .role(employer.getRole())
                .companyAddress(employer.getCompanyAddress())
                .companyName(employer.getCompanyName())
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
}
