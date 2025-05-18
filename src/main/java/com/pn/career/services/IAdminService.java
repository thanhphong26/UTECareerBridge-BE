package com.pn.career.services;

import com.pn.career.dtos.JobCategoryStatDTO;
import com.pn.career.dtos.PackageStatisticDTO;
import com.pn.career.dtos.RevenueByMonth;
import com.pn.career.dtos.UserStatisticDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface IAdminService {
    List<JobCategoryStatDTO> getJobCategoryStats(Integer month, Integer year);
    List<RevenueByMonth> getRevenueByMonth(Integer year);
    List<UserStatisticDTO> getUserStats();
    List<PackageStatisticDTO> getPackageBestSeller();
    List<Map<String, Object>> getForumStatsByDate(LocalDateTime startDate, LocalDateTime endDate);
}
