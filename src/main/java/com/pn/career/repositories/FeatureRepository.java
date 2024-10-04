package com.pn.career.repositories;

import com.pn.career.models.Feature;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeatureRepository extends JpaRepository<Feature, Integer> {
}
