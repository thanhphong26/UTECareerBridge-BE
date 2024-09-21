package com.pn.career.services;

import com.pn.career.dtos.CategoryJobDTO;
import com.pn.career.dtos.CategoryJobUpdateDTO;
import com.pn.career.models.JobCategory;

import java.util.List;

public interface IJobCategoryService {
    List<JobCategory> findAllJobCategories(boolean isAdmin);
    JobCategory getJobCategoryById(Integer jobCategoryId);
    JobCategory createJobCategory(CategoryJobDTO jobCategory);
    JobCategory updateJobCategory(Integer jobCategoryId, CategoryJobUpdateDTO jobCategory);
    void deleteJobCategory(Integer jobCategoryId) throws Exception;
}
