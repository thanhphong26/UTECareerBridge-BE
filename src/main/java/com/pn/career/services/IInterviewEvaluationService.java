package com.pn.career.services;

import com.pn.career.dtos.InterviewEvaluationDTO;
import com.pn.career.responses.InterviewEvaluationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IInterviewEvaluationService {
    InterviewEvaluationResponse createInterviewEvaluation(InterviewEvaluationDTO interviewEvaluationDTO);
    InterviewEvaluationResponse updateInterviewEvaluation(Integer interviewId, InterviewEvaluationDTO interviewEvaluationDTO);
    InterviewEvaluationResponse getInterviewEvaluationById(Integer interviewId);
    void deleteInterviewEvaluation(Integer interviewId);
    InterviewEvaluationResponse getInterviewEvaluationByStudentId(Integer studentId);
    InterviewEvaluationResponse getInterviewEvaluationByInterviewId(Integer interviewId);
    List<InterviewEvaluationResponse> getAllInterviewEvaluationByJobId(Integer jobId);
    Page<InterviewEvaluationResponse> getAllInterviewEvaluationByStudentId(Integer studentId, PageRequest pageRequest);
}
