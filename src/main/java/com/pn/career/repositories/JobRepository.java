package com.pn.career.repositories;

import com.pn.career.models.Job;
import com.pn.career.models.JobCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Integer> {
/*
    List<Job> findAllByEmployerId(int employerId);
*/
    List<Job> findAllByJobCategory(JobCategory jobCategory);
}
