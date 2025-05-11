package com.pn.career.repositories;

import com.pn.career.models.Interview;
import com.pn.career.models.InterviewStatus;
import com.pn.career.models.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface InterviewRepository extends JpaRepository<Interview, Integer> {
    Interview findByMeetingLinkAndEmployerId(String meetingLink, Integer employerId);
    Page<Interview> findByEmployerId(Integer employerId, Pageable pageable);
    @Query("SELECT i FROM Interview i WHERE i.application.resume.student.userId = :studentId AND i.createdAt BETWEEN :startDate AND :endDate ORDER BY i.createdAt DESC")
    List<Interview> findInterviewsByStudentIdAndDateRange(
            @Param("studentId") Integer studentId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    @Query("SELECT i FROM Interview i WHERE i.application.resume.student.userId = :studentId AND i.status = :status AND i.createdAt BETWEEN :startDate AND :endDate ORDER BY i.createdAt DESC")
    List<Interview> findInterviewsByStudentIdAndStatusAndDateRange(
            @Param("studentId") Integer studentId,
            @Param("status") InterviewStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    Integer countByApplication_Job_Employer_UserId(Integer employerId);
    boolean existsByInterviewIdAndStatus(Integer interviewId, InterviewStatus status);
    @Query(value = "SELECT j.job_id FROM interviews i " +
            "JOIN applications a ON a.application_id = i.application_id " +
            "JOIN jobs j ON j.job_id = a.job_id " +
            "WHERE i.status = 'COMPLETED' AND i.employer_id = :employerId " +
            "GROUP BY j.job_id " +
            "ORDER BY MAX(i.schedule_date) DESC",
            nativeQuery = true)
    Page<Integer> findRecentCompletedInterviewJobIdsByEmployerId(@Param("employerId") Integer employerId, Pageable pageable);

}
