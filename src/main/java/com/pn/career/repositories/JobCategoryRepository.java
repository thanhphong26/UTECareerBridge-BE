package com.pn.career.repositories;

import com.pn.career.models.JobCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobCategoryRepository extends JpaRepository<JobCategory, Integer> {
    boolean existsByJobCategoryName(String jobCategoryName);
    JobCategory findByJobCategoryName(String jobCategoryName);
    List<JobCategory> findAllByIsActiveTrue();
}
