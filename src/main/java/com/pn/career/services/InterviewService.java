package com.pn.career.services;

import com.pn.career.dtos.InterviewDTO;
import com.pn.career.dtos.InterviewRequestDTO;
import com.pn.career.models.Application;
import com.pn.career.models.Interview;
import com.pn.career.models.InterviewStatus;
import com.pn.career.repositories.ApplicationRepository;
import com.pn.career.repositories.InterviewRepository;
import com.pn.career.responses.MeetingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InterviewService implements IInterviewService{
    private final InterviewRepository interviewRepository;
    private final ApplicationRepository applicationRepository;
    @Override
    public void scheduleInterview(InterviewDTO interviewDTO) {
    }

    @Override
    public Interview saveInterview(InterviewRequestDTO interviewRequestDTO, MeetingResponse meetingResponse, Integer userId) {
        Application application = applicationRepository.findByResume_ResumeId(interviewRequestDTO.getResumeId());

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


}
