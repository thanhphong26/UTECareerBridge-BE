package com.pn.career.repositories;

import com.pn.career.models.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeRepository extends JpaRepository<Resume, Integer> {
    List<Resume> findAllByStudent_UserId(Integer userId);
}
