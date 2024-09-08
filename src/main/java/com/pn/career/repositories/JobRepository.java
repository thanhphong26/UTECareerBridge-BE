package com.pn.career.repositories;

import com.pn.career.models.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Integer> {
/*
    List<Job> findAllByEmployerId(int employerId);
*/
}
