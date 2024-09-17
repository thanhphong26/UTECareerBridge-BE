package com.pn.career.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BenefitResponse {
    private Integer benefitId;
    private String benefitName;
    private String benefitIcon;
    private String benefitDescription;
}
