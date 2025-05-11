package com.pn.career.repositories;

import com.pn.career.models.InterviewEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewEvaluationRepository extends JpaRepository<InterviewEvaluation, Integer> {
    // get all interview evaluations by job id
    List<InterviewEvaluation> findAllByInterview_Application_Job_JobId(Integer jobId);
    boolean existsByInterview_InterviewId(Integer interviewId);
}
