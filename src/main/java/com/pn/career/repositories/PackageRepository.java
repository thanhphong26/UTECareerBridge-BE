package com.pn.career.repositories;

import com.pn.career.dtos.PackageStatisticDTO;
import com.pn.career.models.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PackageRepository extends JpaRepository<Package, Integer> {
    List<Package> findAllByIsActiveTrue();
    @Query("SELECT new com.pn.career.dtos.PackageStatisticDTO(od.jobPackage.packageId, od.jobPackage.packageName, COUNT(od.jobPackage.packageId)) " +
            "FROM OrderDetail od " +
            "GROUP BY od.jobPackage.packageId " +
            "ORDER BY COUNT(od.jobPackage.packageId) DESC limit 5")
    List<PackageStatisticDTO> getPacakgeBestSeller();

}
