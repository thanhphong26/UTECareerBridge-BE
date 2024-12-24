package com.pn.career.repositories;

import com.pn.career.models.Industry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IndustryRepository extends JpaRepository<Industry, Integer> {
    Industry findByIndustryName(String industryName);
    List<Industry> findAllByIsActiveTrue();
    boolean existsByIndustryNameIgnoreCase(String industryName);
}

