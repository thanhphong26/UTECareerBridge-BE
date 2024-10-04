package com.pn.career.services;

import com.pn.career.models.JobLevel;
import com.pn.career.repositories.JobLevelRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class JobLevelService implements IJobLevelService {
    private final JobLevelRepository jobLevelRepository;
    @Override
    public List<JobLevel> findAllJobLevels(boolean isAdmin) {
        if(isAdmin){
            return jobLevelRepository.findAll();
        }
        return jobLevelRepository.findAllByIsActiveTrue();
    }

    @Override
    public JobLevel getJobLevelById(Integer id) {
        return null;
    }

    @Override
    public JobLevel createJobLevel(JobLevel jobLevel) {
        return null;
    }

    @Override
    public JobLevel updateJobLevel(JobLevel jobLevel) {
        return null;
    }

    @Override
    public void deleteJobLevel(Integer id) {

    }
}
