package com.pn.career.repositories;

import com.pn.career.models.JobCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobCategoryRepository extends JpaRepository<JobCategory, Integer> {
    JobCategory findByJobCategoryName(String jobCategoryName);
    List<JobCategory> findAllByIsActiveTrue();
}
