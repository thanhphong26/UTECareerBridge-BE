package com.pn.career.services;

import com.pn.career.dtos.JobDTO;
import com.pn.career.models.Job;
import com.pn.career.models.JobStatus;
import com.pn.career.responses.JobResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.swing.text.html.Option;
import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

public interface IJobService {
    JobResponse createJob(Integer employerId,JobDTO jobDTO) throws Exception;
    Optional<JobResponse> getJobById(Integer jobId);
    Page<JobResponse> getJobsByEmployerId(Integer employerId, PageRequest page);
    JobResponse updateJob(Integer employerId, Integer jobId, JobDTO jobDTO) throws Exception;
    Page<JobResponse> getAllJobs(JobStatus status, PageRequest pageRequest);
    JobResponse approveJob(Integer jobId);
    JobResponse rejectJob(Integer jobId, String reasonReject);
    JobResponse hideOrEnableJob(Integer employerId, Integer jobId, JobStatus jobStatus);
    Page<JobResponse> searchJob(String keyword, Integer jobCategoryId, Integer industryId, Integer jobLevelId, Integer skillId, String sorting, PageRequest pageRequest);
    Page<JobResponse> getJobByStatus(Integer employerId, JobStatus jobStatus, PageRequest pageRequest);
    void deleteJob(Integer employerId, Integer jobId);
    List<JobResponse> getSimilarJobs(Integer jobId);
}
