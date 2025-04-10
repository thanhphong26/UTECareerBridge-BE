package com.pn.career.services;

import com.pn.career.dtos.InterviewDTO;
import com.pn.career.dtos.InterviewRequestDTO;
import com.pn.career.models.Interview;
import com.pn.career.responses.MeetingResponse;

public interface IInterviewService {
    void scheduleInterview(InterviewDTO interviewDTO);
    Interview saveInterview(InterviewRequestDTO interviewRequestDTO, MeetingResponse meetingResponse, Integer userId);
    Interview getInterviewById(Integer interviewId);
}
