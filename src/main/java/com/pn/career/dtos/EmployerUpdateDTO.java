package com.pn.career.dtos;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployerUpdateDTO {
    @JsonProperty("company_name")
    @NotBlank(message = "Company name is required")
    private String companyName;
    @JsonProperty("company_address")
    private String companyAddress;
    @JsonProperty("company_logo")
    private MultipartFile companyLogo;
    @JsonProperty("company_website")
    private String companyWebsite;
    @JsonProperty("company_description")
    private String companyDescription;
    @JsonProperty("background_image")
    private MultipartFile backgroundImage;
    @JsonProperty("video_introduction")
    private MultipartFile videoIntroduction;
    @JsonProperty("company_size")
    private Integer companySize;
    @JsonProperty("business_certificate")
    private String businessCertificate;
    @JsonProperty("industry_id")
    private Integer industryId;
}
