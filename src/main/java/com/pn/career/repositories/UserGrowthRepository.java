package com.pn.career.repositories;

import com.pn.career.responses.AdminTopEmployerResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface UserGrowthRepository {
    Map<String, Integer> getMonthlyEmployerCounts(LocalDateTime startDate, LocalDateTime endDate);
    Map<String, Integer> getMonthlyStudentCounts(LocalDateTime startDate, LocalDateTime endDate);
    List<Object[]> getAllMonthsInRange(LocalDateTime startDate, LocalDateTime endDate) ;
    List<AdminTopEmployerResponse> getTopEmployers(int limit, LocalDateTime startDate, LocalDateTime endDate);

}
