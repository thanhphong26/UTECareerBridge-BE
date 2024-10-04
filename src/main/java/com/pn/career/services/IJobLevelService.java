package com.pn.career.services;

import com.pn.career.models.JobLevel;

import java.util.List;

public interface IJobLevelService {
    List<JobLevel> findAllJobLevels(boolean isAdmin);
    JobLevel getJobLevelById(Integer id);
    JobLevel createJobLevel(JobLevel jobLevel);
    JobLevel updateJobLevel(JobLevel jobLevel);
    void deleteJobLevel(Integer id);
}
