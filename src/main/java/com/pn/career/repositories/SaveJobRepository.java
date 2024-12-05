package com.pn.career.repositories;

import com.pn.career.models.SaveJob;
import com.pn.career.models.SaveJobId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaveJobRepository extends JpaRepository<SaveJob, SaveJobId> {
    Page<SaveJob> findAllByStudent_UserId(Integer studentId, Pageable pageable);
}
