package com.pn.career.repositories;

import com.pn.career.models.JobLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobLevelRepository extends JpaRepository<JobLevel, Integer>{
    List<JobLevel> findAllByIsActiveTrue();
}
