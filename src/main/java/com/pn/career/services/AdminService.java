package com.pn.career.services;

import com.pn.career.dtos.JobCategoryStatDTO;
import com.pn.career.dtos.PackageStatisticDTO;
import com.pn.career.dtos.RevenueByMonth;
import com.pn.career.dtos.UserStatisticDTO;
import com.pn.career.models.JobCategory;
import com.pn.career.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService implements IAdminService {
    private final JobRepository jobRepository;
    private final JobCategoryRepository jobCategoryRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PackageRepository packageRepository;
    @Override
    public List<JobCategoryStatDTO> getJobCategoryStats(Integer month, Integer year) {
        List<Object[]> jobStats = jobRepository.countJobsByCategory(month, year);
        return jobStats.stream()
                .map(stat -> new JobCategoryStatDTO(
                        ((Number) stat[0]).intValue(),    // jobCategoryId
                        (String) stat[1],                  // jobCategoryName
                        ((Number) stat[2]).longValue()     // jobCount
                ))
                .collect(Collectors.toList());
    }
    @Override
    public List<RevenueByMonth> getRevenueByMonth(Integer year) {
        List<Object[]> revenueStats = orderRepository.getRevenueByMonth(year);
        return revenueStats.stream()
                .map(row -> new RevenueByMonth(
                        ((Number) row[0]).intValue(),  // month
                        year,
                        ((Number) row[2]).longValue()  ,// totalRevenue
                        ((Number) row[1]).intValue()  // packageCount
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserStatisticDTO> getUserStats() {
        Map<String, Object> userStats = userRepository.countUsersByRole();
        return List.of(
                new UserStatisticDTO(
                        ((Number) userStats.get("totalEmployers")).intValue(),
                        ((Number) userStats.get("totalCandidates")).intValue()
                )
        );
    }

    @Override
    public List<PackageStatisticDTO> getPackageBestSeller() {
        List<PackageStatisticDTO> packageStats = packageRepository.getPacakgeBestSeller();
        return packageStats.isEmpty() ? Collections.emptyList() : packageStats;
    }
}
