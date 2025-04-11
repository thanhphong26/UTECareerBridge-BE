package com.pn.career.repositories;

import com.pn.career.models.Interview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewRepository extends JpaRepository<Interview, Integer> {
    Page<Interview> findByEmployerId(Integer employerId, Pageable pageable);
}
