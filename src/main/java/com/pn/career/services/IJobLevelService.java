package com.pn.career.services;

import com.pn.career.models.JobLevel;

import java.util.List;

public interface IJobLevelService {
    List<JobLevel> findAllJobLevels(boolean isAdmin);
}
