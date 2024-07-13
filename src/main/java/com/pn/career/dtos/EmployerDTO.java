package com.pn.career.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.NumberFormat;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmployerDTO {
    @Size(max = 100)
    @NotNull
    @JsonProperty("company_name")
    private String companyName;

    @Size(max = 255)
    @JsonProperty("company_logo")
    private String companyLogo;

    @Size(max = 255)
    @JsonProperty("company_address")
    private String companyAddress;

    @Size(max = 11)
    @JsonProperty("company_phone_number")
    private String companyPhoneNumber;

    @Size(max = 255)
    @JsonProperty("company_website")
    private String companyWebsite;

    @JsonProperty("company_description")
    private String companyDescription;

    @Size(max = 255)
    @JsonProperty("company_image")
    private String companyImage;

    @Size(max = 255)
    @JsonProperty("video_intro")
    private String videoIntro;

    @JsonProperty("company_size")
    private int companySize;

    @Size(max = 255)
    @JsonProperty("business_certificate")
    private String businessCertificate;
}
