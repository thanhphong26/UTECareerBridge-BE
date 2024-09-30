package com.pn.career.repositories;

import com.pn.career.models.Employer;
import com.pn.career.models.Job;
import com.pn.career.models.JobCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
public interface JobRepository extends JpaRepository<Job, Integer> {
    Page<Job> findAllByEmployer(Employer employer, Pageable pageable);
    List<Job> findAllByJobCategory(JobCategory jobCategory);
    @Query("SELECT j FROM Job j WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR j.jobTitle LIKE %:keyword% OR j.jobDescription LIKE %:keyword% OR j.jobLocation LIKE %:keyword%) " +
            "AND (:jobCategoryId IS NULL OR :jobCategoryId = 0 OR j.jobCategory.jobCategoryId = :jobCategoryId) " +
            "AND (:industryId IS NULL OR :industryId = 0 OR j.employer.industry.industryId = :industryId) " +
            "AND (:jobLevelId IS NULL OR :jobLevelId = 0 OR j.jobLevel.jobLevelId = :jobLevelId) " +
            "AND (:skillId IS NULL OR :skillId = 0 OR EXISTS (SELECT js FROM JobSkill js WHERE js.job = j AND js.skill.skillId = :skillId))")
    Page<Job> search(@Param("keyword") String keyword, @Param("jobCategoryId") Integer jobCategoryId, @Param("industryId") Integer industryId, @Param("jobLevelId") Integer jobLevelId, @Param("skillId") Integer skillId, Pageable pageable);
}
