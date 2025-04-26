package com.pn.career.repositories;

import com.pn.career.models.FrequencyEnum;
import com.pn.career.models.JobAlert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobAlertRepository extends JpaRepository<JobAlert, Long> {
    Page<JobAlert> findByUser_UserIdAndActive(Integer userId, boolean active, Pageable pageable);
    List<JobAlert> findByFrequencyAndActive(FrequencyEnum frequency, boolean active);
}
