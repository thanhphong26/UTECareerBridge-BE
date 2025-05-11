package com.pn.career.repositories;

import com.pn.career.models.Application;
import com.pn.career.models.ApplicationStatus;
import com.pn.career.responses.JobStaStudentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

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
    @Query("SELECT a FROM Application a WHERE a.resume.resumeId = :resumeId and a.job.jobId = :jobId")
    Optional<Application> findApplicationByResumeIdAndAndJob_JobId(@Param("resumeId") Integer resumeId, @Param("jobId") Integer jobId);
    @Query("SELECT new com.pn.career.responses.JobStaStudentResponse(" +
            "COUNT(a), " +
            "SUM(CASE WHEN a.applicationStatus = com.pn.career.models.ApplicationStatus.PENDING THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN a.applicationStatus = com.pn.career.models.ApplicationStatus.APPROVED THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN a.applicationStatus = com.pn.career.models.ApplicationStatus.REJECTED THEN 1 ELSE 0 END)) " +
            "FROM Application a " +
            "WHERE a.resume.student.userId = :studentId")
    JobStaStudentResponse getJobStatisticsByStudentId(@Param("studentId") Integer studentId);
    //count all application by employerId
    @Query("SELECT COUNT(a) FROM Application a WHERE a.job.employer.userId = :employerId")
    Integer countAllApplicationByEmployerId(@Param("employerId") Integer employerId);
    boolean existsByResume_ResumeId(Integer resumeId);
}
