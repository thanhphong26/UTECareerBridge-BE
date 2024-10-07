package com.pn.career.repositories;

import com.pn.career.models.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface JobRepository extends JpaRepository<Job, Integer>, JpaSpecificationExecutor<Job> {
    Page<Job> findAllByEmployerAndStatusIn(Employer employer, List<JobStatus> jobStatus,Pageable pageable);
    Page<Job> findAllByEmployer_UserIdAndStatus(Integer employerId, JobStatus status, Pageable pageable);
    List<Job> findAllByJobCategory(JobCategory jobCategory);
    @EntityGraph(attributePaths = {"jobCategory", "employer", "employer.industry", "jobLevel", "jobSkills", "jobSkills.skill"})
    default Page<Job> search(String keyword, Integer categoryId, Integer industryId,
                                     Integer jobLevelId, Integer skillId, Pageable pageable) {
        return findAll((Specification<Job>) (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            // Join các bảng liên quan
            Join<Job, JobCategory> categoryJoin = root.join("jobCategory", JoinType.LEFT);
            Join<Job, Employer> employerJoin = root.join("employer", JoinType.LEFT);
            Join<Employer, Industry> industryJoin = employerJoin.join("industry", JoinType.LEFT);
            Join<Job, JobLevel> jobLevelJoin = root.join("jobLevel", JoinType.LEFT);
            Join<Job, JobSkill> jobSkillJoin = root.join("jobSkills", JoinType.LEFT);
            Join<JobSkill, Skill> skillJoin = jobSkillJoin.join("skill", JoinType.LEFT);
            // Tìm kiếm theo keyword
            if (keyword != null && !keyword.isEmpty()) {
                String[] keywords = keyword.toLowerCase().split("\\s+");
                List<Predicate> keywordPredicates = Arrays.stream(keywords)
                        .map(kw -> cb.or(
                                cb.like(cb.lower(root.get("jobTitle")), "%" + kw + "%"),
                                cb.like(cb.lower(root.get("jobDescription")), "%" + kw + "%"),
                                cb.like(cb.lower(root.get("jobLocation")), "%" + kw + "%")
                        ))
                        .collect(Collectors.toList());
                predicates.add(cb.and(keywordPredicates.toArray(new Predicate[0])));
            }
            // Các điều kiện tìm kiếm khác
            if (categoryId != null && categoryId != 0) {
                predicates.add(cb.equal(categoryJoin.get("jobCategoryId"), categoryId));
            }
            if (industryId != null && industryId != 0) {
                predicates.add(cb.equal(industryJoin.get("industryId"), industryId));
            }
            if (jobLevelId != null && jobLevelId != 0) {
                predicates.add(cb.equal(jobLevelJoin.get("jobLevelId"), jobLevelId));
            }
            if (skillId != null && skillId != 0) {
                predicates.add(cb.equal(skillJoin.get("skillId"), skillId));
            }
            // Chỉ lấy các công việc đang hoạt động
            predicates.add(cb.or(
                    cb.equal(root.get("status"), JobStatus.ACTIVE)
            ));
            query.distinct(true);
            return cb.and( predicates.toArray(new Predicate[0]));
        }, pageable);
    }
}
