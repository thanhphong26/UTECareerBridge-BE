package com.pn.career.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pn.career.models.InterviewEvaluation;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InterviewEvaluationResponse {
    private Integer id;
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
    private String evaluatedByName;
    private String studentName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDateTime createdAt;
    public static InterviewEvaluationResponse fromInterviewEvaluation(InterviewEvaluation interviewEvaluation) {
        return InterviewEvaluationResponse.builder()
                .id(interviewEvaluation.getEvaluationId())
                .interviewId(interviewEvaluation.getInterviewId())
                .technicalSkills(interviewEvaluation.getTechnicalSkills())
                .communicationSkills(interviewEvaluation.getCommunicationSkills())
                .cultureFit(interviewEvaluation.getCultureFit())
                .problemSolving(interviewEvaluation.getProblemSolving())
                .attitude(interviewEvaluation.getAttitude())
                .strengths(interviewEvaluation.getStrengths())
                .weaknesses(interviewEvaluation.getWeaknesses())
                .overallNotes(interviewEvaluation.getOverallNotes())
                .overallRating(interviewEvaluation.getOverallRating())
                .isRecommended(interviewEvaluation.getIsRecommended())
                .recommendedPosition(interviewEvaluation.getRecommendedPosition())
                .recommendedSalary(interviewEvaluation.getRecommendedSalary())
                .evaluatedBy(interviewEvaluation.getEvaluatedBy())
                .createdAt(interviewEvaluation.getCreatedAt())
                .build();
    }
}
