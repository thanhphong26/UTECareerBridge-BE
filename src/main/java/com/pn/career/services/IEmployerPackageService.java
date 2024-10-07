package com.pn.career.services;

import com.pn.career.models.EmployerPackage;
import com.pn.career.models.Order;

import java.util.List;

public interface IEmployerPackageService {
    void createEmployerPackage(Order order);
    void updateEmployerPackage(Integer employerId, Integer packageId);
    List<EmployerPackage> getAllEmployerPackages(Integer employerId);
    EmployerPackage validateExpiredPackage(Integer employerId, Integer packageId);
}
