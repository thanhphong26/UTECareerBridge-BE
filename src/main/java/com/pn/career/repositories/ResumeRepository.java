package com.pn.career.repositories;

import com.pn.career.models.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ResumeRepository extends JpaRepository<Resume, Integer> {
    List<Resume> findAllByStudent_UserId(Integer userId);
    @Query("SELECT r FROM Resume r WHERE r.student.userId = :studentId AND r.createdAt BETWEEN :startDate AND :endDate ORDER BY r.createdAt DESC")
    List<Resume> findResumesByStudentIdAndDateRange(
            @Param("studentId") Integer studentId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
