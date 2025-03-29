package com.pn.career.repositories;

import com.pn.career.models.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewRepository extends JpaRepository<Interview, Integer> {
}
