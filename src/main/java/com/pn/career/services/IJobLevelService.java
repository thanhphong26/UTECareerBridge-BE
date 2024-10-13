package com.pn.career.services;

import com.pn.career.dtos.JobLevelDTO;
import com.pn.career.models.JobLevel;

import java.util.List;

public interface IJobLevelService {
    List<JobLevel> findAllJobLevels(boolean isAdmin);
    JobLevel getJobLevelById(Integer id);
    JobLevel createJobLevel(JobLevelDTO JobLevelDTO);
    JobLevel updateJobLevel(Integer jobLevelId, JobLevelDTO JobLevelDTO);
    void deleteJobLevel(Integer id);
}
