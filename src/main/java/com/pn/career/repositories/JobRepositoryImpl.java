package com.pn.career.repositories;

import com.pn.career.models.*;
import com.pn.career.responses.AdminJobResponse;
import com.pn.career.responses.AdminTopEmployerResponse;
import com.pn.career.responses.TopEmployerResponse;
import com.pn.career.responses.TopSkillResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JobRepositoryImpl implements JobRepositoryCustom{
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public List<TopSkillResponse> getTopApplicantSkillsByEmployerId(Integer employerId, Integer limit, LocalDateTime startDate, LocalDateTime endDate) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<TopSkillResponse> query = cb.createQuery(TopSkillResponse.class);

        // Root entities and joins
        Root<Application> applicationRoot = query.from(Application.class);
        Join<Application, Job> jobJoin = applicationRoot.join("job", JoinType.INNER);
        Join<Application, Resume> resumeJoin = applicationRoot.join("resume", JoinType.INNER);
        Join<Resume, Student> studentJoin = resumeJoin.join("student", JoinType.INNER);
        Join<Student, StudentSkill> studentSkillJoin = studentJoin.join("studentSkills", JoinType.INNER);
        Join<StudentSkill, Skill> skillJoin = studentSkillJoin.join("skill", JoinType.INNER);

        // Where conditions
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(jobJoin.get("employer").get("userId"), employerId));

        if (startDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(applicationRoot.get("createdAt"), startDate));
        }
        if (endDate != null) {
            predicates.add(cb.lessThanOrEqualTo(applicationRoot.get("createdAt"), endDate));
        }

        // Selection, grouping, and ordering
        query.select(cb.construct(
                        TopSkillResponse.class,
                        skillJoin.get("skillId"),
                        skillJoin.get("skillName"),
                        cb.countDistinct(applicationRoot.get("applicationId"))
                ))
                .where(predicates.toArray(new Predicate[0]))
                .groupBy(skillJoin.get("skillId"), skillJoin.get("skillName"))
                .orderBy(cb.desc(cb.countDistinct(applicationRoot.get("applicationId"))));

        // Execute query with limit
        return entityManager.createQuery(query)
                .setMaxResults(limit != null ? limit : 10)
                .getResultList();
    }

    @Override
    public List<TopSkillResponse> getTopSkillsByEmployerId(Integer employerId, int limit, LocalDateTime startDate, LocalDateTime endDate) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<TopSkillResponse> query = cb.createQuery(TopSkillResponse.class);

        Root<JobSkill> jobSkill = query.from(JobSkill.class);
        Join<JobSkill, Skill> skill = jobSkill.join("skill");
        Join<JobSkill, Job> job = jobSkill.join("job");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(job.get("employer").get("userId"), employerId));
        predicates.add(cb.equal(job.get("status"), JobStatus.ACTIVE));

        // Add date range filters
        if (startDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(job.get("createdAt"), startDate));
        }
        if (endDate != null) {
            predicates.add(cb.lessThanOrEqualTo(job.get("createdAt"), endDate));
        }

        query.select(cb.construct(
                        TopSkillResponse.class,
                        skill.get("skillId"),
                        skill.get("skillName"),
                        cb.count(jobSkill)
                ))
                .where(predicates.toArray(new Predicate[0]))
                .groupBy(skill.get("skillId"), skill.get("skillName"))
                .orderBy(cb.desc(cb.count(jobSkill)));

        return entityManager.createQuery(query)
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    public AdminJobResponse getStatisticsJobByAdmin(LocalDateTime startDate, LocalDateTime endDate) {
        Integer activeJobCount = countJobsByStatusAndDateRange(JobStatus.ACTIVE, startDate, endDate);
        Integer pendingJobCount = countJobsByStatusAndDateRange(JobStatus.PENDING, startDate, endDate);
        Integer rejectedJobCount = countJobsByStatusAndDateRange(JobStatus.REJECTED, startDate, endDate);
        Integer totalInterviewCount = countInterviewsByDateRange(startDate, endDate);
        Integer successfulInterviewCount = countInterviewsByStatusAndDateRange(InterviewStatus.COMPLETED, startDate, endDate);
        return AdminJobResponse.builder()
                .activeJob(activeJobCount)
                .pendingJob(pendingJobCount)
                .rejectedJob(rejectedJobCount)
                .countInterview(totalInterviewCount)
                .successfulInterview(successfulInterviewCount)
                .build();
    }

    @Override
    public List<Map<String, Object>> getTopRequestedSkills(int limit, LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT s.skill_name as skill, COUNT(js.job_id) as jobCount " +
                "FROM skills s " +
                "JOIN job_skills js ON s.skill_id = js.skill_id " +
                "JOIN jobs j ON js.job_id = j.job_id " +
                "WHERE j.created_at BETWEEN :startDate AND :endDate " +
                "GROUP BY s.skill_id, s.skill_name " +
                "ORDER BY jobCount DESC " +
                "LIMIT :limit";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        query.setParameter("limit", limit);

        List<Object[]> results = query.getResultList();
        List<Map<String, Object>> topSkills = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> skillInfo = new HashMap<>();
            skillInfo.put("skill", row[0]);
            skillInfo.put("count", ((Number) row[1]).intValue());
            topSkills.add(skillInfo);
        }

        return topSkills;
    }

    private Integer countJobsByStatusAndDateRange(JobStatus status, LocalDateTime startDate, LocalDateTime endDate) {
        String jpql = "SELECT COUNT(j) FROM Job j WHERE j.status = :status " +
                "AND j.createdAt BETWEEN :startDate AND :endDate";

        Query query = entityManager.createQuery(jpql);
        query.setParameter("status", status);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);

        return ((Long) query.getSingleResult()).intValue();
    }

    private Integer countInterviewsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        String jpql = "SELECT COUNT(i) FROM Interview i WHERE " +
                "i.createdAt BETWEEN :startDate AND :endDate";

        Query query = entityManager.createQuery(jpql);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);

        return ((Long) query.getSingleResult()).intValue();
    }

    private Integer countInterviewsByStatusAndDateRange(InterviewStatus status, LocalDateTime startDate, LocalDateTime endDate) {
        String jpql = "SELECT COUNT(i) FROM Interview i WHERE i.status = :status " +
                "AND i.createdAt BETWEEN :startDate AND :endDate";

        Query query = entityManager.createQuery(jpql);
        query.setParameter("status", status);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);

        return ((Long) query.getSingleResult()).intValue();
    }
}
