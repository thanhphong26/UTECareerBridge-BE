package com.pn.career.services;

import com.pn.career.dtos.JobCategoryStatDTO;
import com.pn.career.dtos.PackageStatisticDTO;
import com.pn.career.dtos.RevenueByMonth;
import com.pn.career.dtos.UserStatisticDTO;

import java.util.List;

public interface IAdminService {
    List<JobCategoryStatDTO> getJobCategoryStats(Integer month, Integer year);
    List<RevenueByMonth> getRevenueByMonth(Integer year);
    List<UserStatisticDTO> getUserStats();
    List<PackageStatisticDTO> getPackageBestSeller();
}
