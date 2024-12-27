package com.pn.career.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pn.career.models.Industry;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IndustryResponse {
    private int industryId;
    private String industryName;
    public static IndustryResponse fromIndustry(Industry industry) {
        return IndustryResponse.builder()
                .industryId(industry.getIndustryId())
                .industryName(industry.getIndustryName())
                .build();
    }
}
