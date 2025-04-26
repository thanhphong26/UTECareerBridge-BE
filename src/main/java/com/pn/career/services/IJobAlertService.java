package com.pn.career.services;

import com.pn.career.dtos.JobAlertDTO;
import com.pn.career.responses.JobAlertResponse;
import com.pn.career.responses.JobResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IJobAlertService {
    JobAlertResponse createJobAlert(JobAlertDTO jobAlertDTO, Integer userId);
    JobAlertResponse updateJobAlert(Long id, JobAlertDTO jobAlertDTO, Integer userId);
    JobAlertResponse getJobAlertById(Long id);
    void deleteJobAlert(Long id, Integer userId);
    Page<JobAlertResponse> getJobAlertByUserIdAndActive(Integer userId, boolean active, PageRequest pageRequest);
    List<JobResponse> getJobAlertByUser(JobAlertDTO jobAlertDTO, Integer userId);
}

