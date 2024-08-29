package com.pn.career.repositories;

import com.pn.career.models.Industry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndustryRepository extends JpaRepository<Industry, Integer> {
    Industry findByIndustryName(String industryName);
}

