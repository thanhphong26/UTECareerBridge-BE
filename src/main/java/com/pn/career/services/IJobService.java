package com.pn.career.services;

import com.pn.career.dtos.JobDTO;
import com.pn.career.models.Job;

import java.util.List;
import java.util.Optional;

public interface IJobService {
    List<Job> findAllJobs(boolean isAdmin);
    Job createJob(Integer employerId,JobDTO jobDTO) throws Exception;
}
