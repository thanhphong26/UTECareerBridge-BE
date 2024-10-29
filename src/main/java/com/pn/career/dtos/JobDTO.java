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
    private Integer jobCategoryId;
    private Integer jobLevelId;
    private String jobTitle;
    private String jobDescription;
    private String jobRequirements;
    private String jobLocation;
    private BigDecimal jobMinSalary;
    private BigDecimal jobMaxSalary;
    private int amount;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate jobDeadline;
    private Set<Integer> skillIds;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer packageId;
}
