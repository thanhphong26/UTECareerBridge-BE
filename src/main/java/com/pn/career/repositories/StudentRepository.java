package com.pn.career.repositories;

import com.pn.career.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    List<Student> findAllByRole_RoleName(String role);
}
