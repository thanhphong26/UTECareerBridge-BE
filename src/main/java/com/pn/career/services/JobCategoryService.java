package com.pn.career.services;

import com.pn.career.models.JobCategory;
import com.pn.career.repositories.JobCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class JobCategoryService implements IJobCategoryService{
    private final JobCategoryRepository jobCategoryRepository;
    @Override
    public List<JobCategory> findAllJobCategories(boolean isAdmin) {
        if(isAdmin){
            return jobCategoryRepository.findAll();
        }
        return jobCategoryRepository.findAllByIsActiveTrue();
    }
}
