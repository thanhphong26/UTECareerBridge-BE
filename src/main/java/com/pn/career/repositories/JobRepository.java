package com.pn.career.repositories;

import com.pn.career.models.Employer;
import com.pn.career.models.Job;
import com.pn.career.models.JobCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Integer> {
    Page<Job> findAllByEmployer(Employer employer, Pageable pageable);
    List<Job> findAllByJobCategory(JobCategory jobCategory);
}
