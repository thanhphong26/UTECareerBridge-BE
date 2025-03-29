package com.pn.career.services;

import com.pn.career.dtos.InterviewDTO;
import com.pn.career.repositories.InterviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InterviewService implements IInterviewService{
    private final InterviewRepository interviewRepository;
    @Override
    public void scheduleInterview(InterviewDTO interviewDTO) {


    }
}
