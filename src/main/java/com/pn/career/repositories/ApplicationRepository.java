package com.pn.career.repositories;

import com.pn.career.models.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ApplicationRepository extends JpaRepository<Application, Integer> {
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Application a " +
            "WHERE a.job.jobId = :jobId AND a.resume.student.userId = :studentId")
    boolean existsByJobIdAndStudentId(@Param("jobId") Integer jobId, @Param("studentId") Integer studentId);}
