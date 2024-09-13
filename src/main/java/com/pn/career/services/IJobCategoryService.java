package com.pn.career.services;

import com.pn.career.dtos.CategoryJobDTO;
import com.pn.career.models.JobCategory;

import java.util.List;

public interface IJobCategoryService {
    List<JobCategory> findAllJobCategories(boolean isAdmin);
    JobCategory createJobCategory(CategoryJobDTO jobCategory);
}
