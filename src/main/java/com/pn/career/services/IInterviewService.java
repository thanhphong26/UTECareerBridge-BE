package com.pn.career.services;

import com.pn.career.dtos.InterviewDTO;
import com.pn.career.dtos.InterviewRequestDTO;
import com.pn.career.models.Interview;
import com.pn.career.models.InterviewStatus;
import com.pn.career.responses.InterviewResponse;
import com.pn.career.responses.MeetingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface IInterviewService {
    void scheduleInterview(InterviewDTO interviewDTO);
    Interview saveInterview(InterviewRequestDTO interviewRequestDTO, MeetingResponse meetingResponse, Integer userId);
    InterviewResponse getInterviewById(Integer interviewId);
    Page<InterviewResponse> getInterviewsByEmployerId(Integer employerId, PageRequest pageRequest);
    InterviewResponse updateInterviewStatus(Integer interviewId, InterviewStatus status);
}
