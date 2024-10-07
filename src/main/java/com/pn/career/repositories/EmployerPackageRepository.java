package com.pn.career.repositories;

import com.pn.career.models.EmployerPackage;
import com.pn.career.models.EmployerPackageId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployerPackageRepository extends JpaRepository<EmployerPackage, EmployerPackageId> {
    List<EmployerPackage> findAllByEmployer_UserId(Integer employerId);
}
