package com.pn.career.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobDTO {
    @JsonProperty("category_id")
    private Integer jobCategoryId;
    @JsonProperty("level_id")
    private Integer jobLevelId;
    @JsonProperty("job_title")
    private String jobTitle;
    @JsonProperty("job_description")
    private String jobDescription;
    @JsonProperty("job_requirements")
    private String jobRequirements;
    @JsonProperty("job_location")
    private String jobLocation;
    @JsonProperty("job_min_salary")
    private BigDecimal jobMinSalary;
    @JsonProperty("job_max_salary")
    private BigDecimal jobMaxSalary;
    @JsonProperty("amount")
    private int amount;
    @JsonProperty("job_deadline")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate jobDeadline;
    private Set<Integer> skillIds;
}
