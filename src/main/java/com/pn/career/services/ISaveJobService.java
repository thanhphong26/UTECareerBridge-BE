package com.pn.career.services;

import com.pn.career.responses.JobResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ISaveJobService {
    void saveJob(Integer studentId, Integer jobId);
    void unsaveJob(Integer studentId, Integer jobId);
    boolean isSaved(Integer studentId, Integer jobId);
    Page<JobResponse> getSavedJobs(Integer studentId, PageRequest pageRequest);
}
