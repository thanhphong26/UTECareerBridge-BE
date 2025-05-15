package com.pn.career.services;

import com.pn.career.dtos.InterviewEvaluationDTO;
import com.pn.career.exceptions.PermissionDenyException;
import com.pn.career.models.*;
import com.pn.career.repositories.EmployerRepository;
import com.pn.career.repositories.InterviewEvaluationRepository;
import com.pn.career.repositories.InterviewRepository;
import com.pn.career.repositories.StudentRepository;
import com.pn.career.responses.InterviewEvaluationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewEvaluationService implements IInterviewEvaluationService{
    private final InterviewEvaluationRepository evaluationRepository;
    private final InterviewRepository interviewRepository;
    private final EmployerRepository employerRepository;
    private final StudentRepository studentRepository;
    @Override
    public InterviewEvaluationResponse createInterviewEvaluation(InterviewEvaluationDTO interviewEvaluationDTO) {
        if(!interviewRepository.existsByInterviewIdAndStatus(interviewEvaluationDTO.getInterviewId(), InterviewStatus.COMPLETED)){
            throw new PermissionDenyException("Không thể tạo đánh giá phỏng vấn cho ứng viên này vì chưa hoàn thành phỏng vấn");
        }
        if(evaluationRepository.existsByInterview_InterviewId(interviewEvaluationDTO.getInterviewId())){
            throw new PermissionDenyException("Đánh giá phỏng vấn đã tồn tại");
        }
        InterviewEvaluation interviewEvaluation = InterviewEvaluation.builder()
                .interviewId(interviewEvaluationDTO.getInterviewId())
                .technicalSkills(interviewEvaluationDTO.getTechnicalSkills())
                .communicationSkills(interviewEvaluationDTO.getCommunicationSkills())
                .cultureFit(interviewEvaluationDTO.getCultureFit())
                .problemSolving(interviewEvaluationDTO.getProblemSolving())
                .attitude(interviewEvaluationDTO.getAttitude())
                .strengths(interviewEvaluationDTO.getStrengths())
                .weaknesses(interviewEvaluationDTO.getWeaknesses())
                .overallNotes(interviewEvaluationDTO.getOverallNotes())
                .overallRating(interviewEvaluationDTO.getOverallRating())
                .isRecommended(interviewEvaluationDTO.getIsRecommended())
                .recommendedPosition(interviewEvaluationDTO.getRecommendedPosition())
                .recommendedSalary(interviewEvaluationDTO.getRecommendedSalary())
                .evaluatedBy(interviewEvaluationDTO.getEvaluatedBy())
                .build();
        interviewEvaluation = evaluationRepository.save(interviewEvaluation);
        return fromInterviewEvaluation(interviewEvaluation);
    }

    @Override
    public InterviewEvaluationResponse updateInterviewEvaluation(Integer interviewId, InterviewEvaluationDTO interviewEvaluationDTO) {
        InterviewEvaluationResponse interviewEvaluation = getInterviewEvaluationById(interviewId);
        InterviewEvaluation updatedInterviewEvaluation = InterviewEvaluation.builder()
                .evaluationId(interviewEvaluation.getId())
                .interviewId(interviewEvaluationDTO.getInterviewId())
                .technicalSkills(interviewEvaluationDTO.getTechnicalSkills())
                .communicationSkills(interviewEvaluationDTO.getCommunicationSkills())
                .cultureFit(interviewEvaluationDTO.getCultureFit())
                .problemSolving(interviewEvaluationDTO.getProblemSolving())
                .attitude(interviewEvaluationDTO.getAttitude())
                .strengths(interviewEvaluationDTO.getStrengths())
                .weaknesses(interviewEvaluationDTO.getWeaknesses())
                .overallNotes(interviewEvaluationDTO.getOverallNotes())
                .overallRating(interviewEvaluationDTO.getOverallRating())
                .isRecommended(interviewEvaluationDTO.getIsRecommended())
                .recommendedPosition(interviewEvaluationDTO.getRecommendedPosition())
                .recommendedSalary(interviewEvaluationDTO.getRecommendedSalary())
                .evaluatedBy(interviewEvaluationDTO.getEvaluatedBy())
                .build();
        evaluationRepository.save(updatedInterviewEvaluation);
        return fromInterviewEvaluation(updatedInterviewEvaluation);
    }

    @Override
    public InterviewEvaluationResponse getInterviewEvaluationById(Integer interviewId) {
        InterviewEvaluation interviewEvaluation = evaluationRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đánh giá phỏng vấn có id " + interviewId));
        return fromInterviewEvaluation(interviewEvaluation);
    }

    @Override
    public void deleteInterviewEvaluation(Integer interviewId) {
        InterviewEvaluation interviewEvaluation = evaluationRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đánh giá phỏng vấn có id " + interviewId));
        evaluationRepository.delete(interviewEvaluation);
    }

    @Override
    public InterviewEvaluationResponse getInterviewEvaluationByStudentId(Integer studentId) {
        return null;
    }

    @Override
    public InterviewEvaluationResponse getInterviewEvaluationByInterviewId(Integer interviewId) {
        return null;
    }

    @Override
    public List<InterviewEvaluationResponse> getAllInterviewEvaluationByJobId(Integer jobId) {
        List<InterviewEvaluation> interviewEvaluations = evaluationRepository.findAllByInterview_Application_Job_JobId(jobId);
        if(interviewEvaluations.isEmpty()){
            throw new RuntimeException("Không tìm thấy đánh giá phỏng vấn cho công việc có id " + jobId);
        }
        List<InterviewEvaluationResponse> interviewEvaluationResponses = interviewEvaluations.stream()
                .map(this::fromInterviewEvaluation)
                .toList();
        return interviewEvaluationResponses;
    }
    private InterviewEvaluationResponse fromInterviewEvaluation(InterviewEvaluation interviewEvaluation){
        InterviewEvaluationResponse interviewEvaluationResponse = InterviewEvaluationResponse.fromInterviewEvaluation(interviewEvaluation);
        Employer employer = employerRepository.findById(interviewEvaluation.getEvaluatedBy()).orElse(null);
        String nameEvaluatedBy = employer != null ? employer.getCompanyName() : "Unknown";
        Interview interview = interviewRepository.findById(interviewEvaluation.getInterviewId()).orElse(null);
        Student student = studentRepository.findById(interview.getApplication().getResume().getStudent().getUserId()).orElse(null);
        String studentName = student != null ? student.getLastName() + "" + student.getFirstName() : "Unknown";
        interviewEvaluationResponse.setEvaluatedByName(nameEvaluatedBy);
        interviewEvaluationResponse.setStudentName(studentName);
        return interviewEvaluationResponse;
    }
}
