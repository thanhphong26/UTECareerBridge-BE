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
    @JsonProperty("industry_id")
    private int industryId;
    @JsonProperty("industry_name")
    private String industryName;
    public static IndustryResponse fromIndustry(Industry industry) {
        return IndustryResponse.builder()
                .industryId(industry.getIndustryId())
                .industryName(industry.getIndustryName())
                .build();
    }
}
