package com.pn.career.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobRecommendResponse {
    @JsonProperty("job_id")
    private int jobId;
    private float score;
    @JsonProperty("job_title")
    private String jobTitle;
    private String logo;
    @JsonProperty("company_name")
    private String companyName;
    @JsonProperty("job_min_salary")
    private BigDecimal jobMinSalary;
    @JsonProperty("job_max_salary")
    private BigDecimal jobMaxSalary;
}
