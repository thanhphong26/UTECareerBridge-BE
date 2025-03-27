package com.pn.career.dtos;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployerUpdateDTO {
    private String companyName;
    private String companyAddress;
    private String companyEmail;
    private String companyLogo;
    private String companyWebsite;
    private String companyDescription;
    private String backgroundImage;
    private String videoIntroduction;
    private String companySize;
    private Integer industryId;
    private List<BenefitDetailDTO> benefitDetails;
}
