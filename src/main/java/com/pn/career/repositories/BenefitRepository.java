package com.pn.career.repositories;

import com.pn.career.models.Benefit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BenefitRepository extends JpaRepository<Benefit, Integer>{
    List<Benefit> findAllByIsActiveTrue();
    boolean existsByBenefitNameIgnoreCase(String benefitName);
}
