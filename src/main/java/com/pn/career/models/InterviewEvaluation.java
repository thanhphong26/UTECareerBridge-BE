package com.pn.career.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Table(name = "interview_evaluations")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
public class InterviewEvaluation extends BaseEntity{
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "evaluation_id")
    private Integer evaluationId;
    @Column(name = "interview_id")
    private Integer interviewId;

    @Column(name = "technical_skills", nullable = false)
    private Integer technicalSkills;

    @Column(name = "communication_skills", nullable = false)
    private Integer communicationSkills;

    @Column(name = "culture_fit", nullable = false)
    private Integer cultureFit;

    @Column(name = "problem_solving", nullable = false)
    private Integer problemSolving;

    @Column(name = "attitude", nullable = false)
    private Integer attitude;

    @Column(name = "strengths")
    private String strengths;

    @Column(name = "weaknesses")
    private String weaknesses;

    @Column(name = "overall_notes")
    private String overallNotes;

    @Column(name = "overall_rating", nullable = false)
    private Integer overallRating;

    @Column(name = "is_recommended")
    private Boolean isRecommended = false;

    @Column(name = "recommended_position")
    private String recommendedPosition;

    @Column(name = "recommended_salary", precision = 18, scale = 2)
    private BigDecimal recommendedSalary;

    @Column(name = "evaluated_by", nullable = false)
    private Integer evaluatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "interview_id", insertable = false, updatable = false)
    private Interview interview;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "evaluated_by", insertable = false, updatable = false)
    private User evaluator;

}
