package com.pn.career.repositories;

import com.pn.career.responses.AdminTopEmployerResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserGrowthRepositoryImpl implements UserGrowthRepository {
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public Map<String, Integer> getMonthlyEmployerCounts(LocalDateTime startDate, LocalDateTime endDate) {
        String jpql = "SELECT YEAR(e.createdAt), MONTH(e.createdAt), COUNT(e) " +
                "FROM Employer e " +
                "WHERE e.createdAt BETWEEN :startDate AND :endDate " +
                "GROUP BY YEAR(e.createdAt), MONTH(e.createdAt) " +
                "ORDER BY YEAR(e.createdAt), MONTH(e.createdAt)";

        Query query = entityManager.createQuery(jpql);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);

        List<Object[]> results = query.getResultList();
        Map<String, Integer> countsByMonth = new HashMap<>();

        for (Object[] row : results) {
            Integer year = (Integer) row[0];
            Integer month = (Integer) row[1];
            Long count = (Long) row[2];

            String key = year + "-" + (month < 10 ? "0" + month : month);
            countsByMonth.put(key, count.intValue());
        }

        return countsByMonth;
    }

    @Override
    public Map<String, Integer> getMonthlyStudentCounts(LocalDateTime startDate, LocalDateTime endDate) {
        String jpql = "SELECT YEAR(s.createdAt), MONTH(s.createdAt), COUNT(s) " +
                "FROM Student s " +
                "WHERE s.createdAt BETWEEN :startDate AND :endDate " +
                "GROUP BY YEAR(s.createdAt), MONTH(s.createdAt) " +
                "ORDER BY YEAR(s.createdAt), MONTH(s.createdAt)";

        Query query = entityManager.createQuery(jpql);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);

        List<Object[]> results = query.getResultList();
        Map<String, Integer> countsByMonth = new HashMap<>();

        for (Object[] row : results) {
            Integer year = (Integer) row[0];
            Integer month = (Integer) row[1];
            Long count = (Long) row[2];

            String key = year + "-" + (month < 10 ? "0" + month : month);
            countsByMonth.put(key, count.intValue());
        }

        return countsByMonth;
    }

    @Override
    public List<Object[]> getAllMonthsInRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> result = new ArrayList<>();

        // Create a list of all year-month combinations in the range
        LocalDateTime current = startDate.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime end = endDate.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

        while (!current.isAfter(end)) {
            Object[] yearMonth = new Object[2];
            yearMonth[0] = current.getYear();
            yearMonth[1] = current.getMonthValue();
            result.add(yearMonth);

            // Move to next month
            current = current.plusMonths(1);
        }

        return result;
    }

    @Override
    public List<AdminTopEmployerResponse> getTopEmployers(int limit, LocalDateTime startDate, LocalDateTime endDate) {
        String sql = """
            SELECT e.employer_id as id, e.company_name as name, 
                   COUNT(DISTINCT j.job_id) as jobPosted,
                   COUNT(DISTINCT a.application_id) as hires
            FROM employers e
            LEFT JOIN jobs j ON e.employer_id = j.employer_id AND j.created_at BETWEEN :startDate AND :endDate
            LEFT JOIN applications a ON j.job_id = a.job_id AND a.application_status = 'HIRED' AND a.created_at BETWEEN :startDate AND :endDate
            GROUP BY e.employer_id, e.company_name
            ORDER BY jobPosted DESC, hires DESC
            LIMIT :limit
            """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        query.setParameter("limit", limit);

        List<Object[]> results = query.getResultList();
        List<AdminTopEmployerResponse> topEmployers = new ArrayList<>();

        for (Object[] row : results) {
            topEmployers.add(AdminTopEmployerResponse.builder()
                    .employerId((Integer) row[0])
                    .name((String) row[1])
                    .jobPosted(((Number) row[2]).intValue())
                    .hires(((Number) row[3]).intValue())
                    .build());
        }
        return topEmployers;
    }
}
