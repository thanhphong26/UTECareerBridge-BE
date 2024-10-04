package com.pn.career.repositories;

import com.pn.career.models.Package;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PackageRepository extends JpaRepository<Package, Integer> {
    List<Package> findAllByIsActiveTrue();
}
