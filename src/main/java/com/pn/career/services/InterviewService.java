package com.pn.career.services;

import com.pn.career.dtos.InterviewDTO;
import com.pn.career.dtos.InterviewRequestDTO;
import com.pn.career.models.Application;
import com.pn.career.models.Interview;
import com.pn.career.models.InterviewStatus;
import com.pn.career.repositories.ApplicationRepository;
import com.pn.career.repositories.InterviewRepository;
import com.pn.career.responses.InterviewResponse;
import com.pn.career.responses.MeetingResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    @Transactional
    public Interview saveInterview(InterviewRequestDTO interviewRequestDTO, MeetingResponse meetingResponse, Integer userId) {
        Application application = applicationRepository.findApplicationByResumeIdAndAndJob_JobId(interviewRequestDTO.getResumeId(), interviewRequestDTO.getJobId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hồ sơ ứng tuyển"));
        log.info("Meeting Response: {}", application);
        //check if interview already exists
        if (interviewRepository.findByMeetingLinkAndEmployerId(meetingResponse.getJoinUrl(), userId) != null) {
            throw new EntityNotFoundException("Cuộc phỏng vấn đã tồn tại");
        }
        Interview interview = Interview.builder()
                .application(application)
                .scheduleDate(interviewRequestDTO.getStartTime())
                .duration(interviewRequestDTO.getDurationMinutes())
                .meetingLink(meetingResponse.getJoinUrl())
                .status(InterviewStatus.SCHEDULED)
                .employerId(userId)
                .build();

        Interview savedInterview = interviewRepository.save(interview);
        return savedInterview;
    }

    @Override
    public InterviewResponse getInterviewById(Integer interviewId) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy cuộc phỏng vấn với ID: " + interviewId));

        return InterviewResponse.builder()
                .interviewId(interview.getInterviewId())
                .studentName(interview.getApplication().getResume().getStudent().getLastName() + " " +
                        interview.getApplication().getResume().getStudent().getFirstName())
                .scheduleDate(interview.getScheduleDate())
                .duration(interview.getDuration())
                .meetingLink(interview.getMeetingLink())
                .status(interview.getStatus())
                .build();
    }

    @Override
    public Page<InterviewResponse> getInterviewsByEmployerId(Integer employerId, PageRequest pageRequest) {
        try {
            log.info("Fetching interviews for employer ID: {}", employerId);
            Page<Interview> interviews = interviewRepository.findByEmployerId(employerId, pageRequest);
            log.info("Found {} interviews", interviews.getTotalElements());

            return interviews.map(interview -> {
                try {
                    // Your mapping logic
                    return InterviewResponse.builder()
                            .interviewId(interview.getInterviewId())
                            .studentName(interview.getApplication().getResume().getStudent().getLastName() + " " +
                                    interview.getApplication().getResume().getStudent().getFirstName())
                            .scheduleDate(interview.getScheduleDate())
                            .duration(interview.getDuration())
                            .meetingLink(interview.getMeetingLink())
                            .status(interview.getStatus())
                            .build();
                } catch (Exception e) {
                    log.error("Error mapping interview with ID {}: {}", interview.getInterviewId(), e.getMessage(), e);
                    // Return a partial response or throw a more specific exception
                    return InterviewResponse.builder()
                            .interviewId(interview.getInterviewId())
                            .scheduleDate(interview.getScheduleDate())
                            .duration(interview.getDuration())
                            .meetingLink(interview.getMeetingLink())
                            .status(interview.getStatus())
                            .studentName("[Error loading student info]")
                            .build();
                }
            });
        } catch (Exception e) {
            log.error("Error fetching interviews for employer ID {}: {}", employerId, e.getMessage(), e);
            throw e; // Or handle appropriately
        }
    }

    @Override
    @Transactional
    public InterviewResponse updateInterviewStatus(Integer interviewId, InterviewStatus status) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy cuộc phỏng vấn với ID: " + interviewId));
        interview.setStatus(status);
        Interview updatedInterview = interviewRepository.save(interview);
        return InterviewResponse.builder()
                .interviewId(updatedInterview.getInterviewId())
                .studentName(updatedInterview.getApplication().getResume().getStudent().getLastName() + " " +
                        updatedInterview.getApplication().getResume().getStudent().getFirstName())
                .scheduleDate(updatedInterview.getScheduleDate())
                .duration(updatedInterview.getDuration())
                .meetingLink(updatedInterview.getMeetingLink())
                .status(updatedInterview.getStatus())
                .build();
    }


}
