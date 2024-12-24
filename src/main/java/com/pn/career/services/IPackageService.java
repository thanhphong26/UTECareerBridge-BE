package com.pn.career.services;

import com.pn.career.dtos.PackageDTO;
import com.pn.career.models.Package;
import com.pn.career.responses.PackageResponse;

import java.util.List;

public interface IPackageService {
    Package getPackageById(Integer packageId);
    Package createPackage(PackageDTO packageDTO);
    Package updatePackage(Integer packageId, PackageDTO packageDTO);
    void deletePackage(Integer packageId);
    List<Package> getAllPackages(boolean isAdmin);
}
