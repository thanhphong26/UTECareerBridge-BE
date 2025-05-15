package com.pn.career.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InterviewEvaluationDTO {
    private Integer interviewId;
    private Integer technicalSkills;
    private Integer communicationSkills;
    private Integer cultureFit;
    private Integer problemSolving;
    private Integer attitude;
    private String strengths;
    private String weaknesses;
    private String overallNotes;
    private Integer overallRating;
    private Boolean isRecommended;
    private String recommendedPosition;
    private BigDecimal recommendedSalary;
    private Integer evaluatedBy;
}
