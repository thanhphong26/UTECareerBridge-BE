package com.pn.career.repositories;

import com.pn.career.models.CVTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CVTemplateRepository extends JpaRepository<CVTemplate, Integer> {
}
