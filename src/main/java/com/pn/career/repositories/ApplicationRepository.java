package com.pn.career.repositories;

import com.pn.career.models.Application;
import com.pn.career.models.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ApplicationRepository extends JpaRepository<Application, Integer> {
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Application a " +
            "WHERE a.job.jobId = :jobId AND a.resume.student.userId = :studentId")
    boolean existsByJobIdAndStudentId(@Param("jobId") Integer jobId, @Param("studentId") Integer studentId);
    Page<Application> findAllByJob_JobIdAndApplicationStatus(Integer jobId, ApplicationStatus status, Pageable pageable);
    @Query("SELECT a FROM Application a WHERE a.resume.student.userId = :studentId ORDER BY a.createdAt DESC")
    Page<Application> findAppliedApplicationsByStudentIdOrderedByDate(
            @Param("studentId") int studentId, Pageable pageable
    );
    @Query("SELECT COUNT(DISTINCT a.resume.student) FROM Application a " +
            "JOIN a.job j " +
            "WHERE j.employer.userId = :employerId")
    Integer countUniqueStudentApplicationsByEmployer(@Param("employerId") int employerId);
    Application findByResume_ResumeId(Integer resumeId);
}
