package com.pn.career.repositories;

import com.pn.career.models.Employer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployerRepository extends JpaRepository<Employer, Integer> {
    Optional<Employer> findByCompanyName(String companyName);
}
