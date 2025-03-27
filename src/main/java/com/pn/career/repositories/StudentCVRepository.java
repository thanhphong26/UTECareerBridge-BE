package com.pn.career.repositories;

import com.pn.career.models.StudentCV;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentCVRepository extends JpaRepository<StudentCV, Integer> {
    List<StudentCV> findAllByStudent_UserId(Integer studentId);
}
