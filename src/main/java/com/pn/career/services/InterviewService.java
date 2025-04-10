package com.pn.career.services;

import com.pn.career.dtos.InterviewDTO;
import com.pn.career.dtos.InterviewRequestDTO;
import com.pn.career.models.Application;
import com.pn.career.models.Interview;
import com.pn.career.models.InterviewStatus;
import com.pn.career.repositories.ApplicationRepository;
import com.pn.career.repositories.InterviewRepository;
import com.pn.career.responses.MeetingResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewService implements IInterviewService{
    private final InterviewRepository interviewRepository;
    private final ApplicationRepository applicationRepository;
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public void scheduleInterview(InterviewDTO interviewDTO) {
    }

    @Override
    public Interview saveInterview(InterviewRequestDTO interviewRequestDTO, MeetingResponse meetingResponse, Integer userId) {
        Application application = applicationRepository.findApplicationByResumeIdAndAndJob_JobId(interviewRequestDTO.getResumeId(), interviewRequestDTO.getJobId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hồ sơ ứng tuyển"));
        log.info("Meeting Response: {}", application);
        Interview interview = Interview.builder()
                .application(application)
                .scheduleDate(interviewRequestDTO.getStartTime())
                .duration(interviewRequestDTO.getDurationMinutes())
                .meetingLink(meetingResponse.getJoinUrl())
                .status(InterviewStatus.SCHEDULED)
                .build();

        Interview savedInterview = interviewRepository.save(interview);
        return savedInterview;
    }

    @Override
    public Interview getInterviewById(Integer interviewId) {
        return interviewRepository.findById(interviewId).orElse(null);
    }


}
