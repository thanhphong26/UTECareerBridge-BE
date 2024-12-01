package com.pn.career.repositories;

import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.models.*;
import com.pn.career.models.Package;
import jakarta.persistence.criteria.*;
import jakarta.persistence.criteria.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;
import java.util.stream.Collectors;

public interface JobRepository extends JpaRepository<Job, Integer>, JpaSpecificationExecutor<Job> {
    Job findJobByJobIdAndStatus(Integer jobId, JobStatus status);
    Page<Job> findAllByEmployerAndStatusIn(Employer employer, List<JobStatus> jobStatus,Pageable pageable);
    Page<Job> findAllByEmployer_UserIdAndStatus(Integer employerId, JobStatus status, Pageable pageable);
    List<Job> findAllByJobCategory(JobCategory jobCategory);
    @EntityGraph(attributePaths = {"jobCategory", "employer", "employer.industry", "jobLevel", "jobSkills", "jobSkills.skill"})
    default Page<Job> search(String keyword, Integer categoryId, Integer industryId,
                                     Integer jobLevelId, Integer skillId, String sorting, Pageable pageable) {
        return findAll((Specification<Job>) (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            // Join các bảng liên quan
            Join<Job, JobCategory> categoryJoin = root.join("jobCategory", JoinType.LEFT);
            Join<Job, Employer> employerJoin = root.join("employer", JoinType.LEFT);
            Join<Employer, Industry> industryJoin = employerJoin.join("industry", JoinType.LEFT);
            Join<Job, JobLevel> jobLevelJoin = root.join("jobLevel", JoinType.LEFT);
            Join<Job, JobSkill> jobSkillJoin = root.join("jobSkills", JoinType.LEFT);
            Join<JobSkill, Skill> skillJoin = jobSkillJoin.join("skill", JoinType.LEFT);

            Subquery<Object> featurePrioritySubquery = query.subquery(Object.class);
            Root<Package> packageRoot = featurePrioritySubquery.from(Package.class);
            Join<Package, Feature> featureJoin = packageRoot.join("feature", JoinType.INNER);

            featurePrioritySubquery.select(cb.coalesce(
                    cb.selectCase()
                            .when(cb.equal(featureJoin.get("featureId"), 1), 3)
                            .when(cb.equal(featureJoin.get("featureId"), 2), 2)
                            .when(cb.equal(featureJoin.get("featureId"), 3), 1)
                            .otherwise(0),
                    0
            )).where(cb.equal(packageRoot.get("packageId"), root.get("packageId")));
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
            List<Order> orders = new ArrayList<>();
            orders.add(cb.desc(featurePrioritySubquery));
            orders.add(cb.desc(cb.selectCase()
                    .when(cb.isNotNull(root.get("packageId")), 1)
                    .otherwise(0)));

            //Sorting
            if (sorting != null && !sorting.isEmpty()) {
                switch (sorting.toLowerCase()) {
                    case "newest":
                        orders.add(cb.desc(root.get("createdAt")));
                        break;
                    case "oldest":
                        orders.add(cb.asc(root.get("createdAt")));
                        break;
                    case "salary_desc":
                        orders.add(cb.desc(root.get("jobMaxSalary")));
                        break;
                    case "salary_asc":
                        orders.add(cb.asc(root.get("jobMinSalary")));
                        break;
                    default:
                        orders.add(cb.desc(root.get("createdAt")));
                        break;
                }
            }
            query.orderBy(orders);
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }
    //Get Jobs by jobSkills.skillId
    default List<Job> findAllByJobSkills_SkillId(Integer skillId) {
        return findAll((Specification<Job>) (root, query, cb) -> {
            Join<Job, JobSkill> jobSkillJoin = root.join("jobSkills", JoinType.LEFT);
            Join<JobSkill, Skill> skillJoin = jobSkillJoin.join("skill", JoinType.LEFT);
            return cb.equal(skillJoin.get("skillId"), skillId);
        });
    }
    default List<Job> findAllByJobSkillsAndJobSkillsIn(List<Integer> skillIds) {
        if (skillIds == null || skillIds.isEmpty()) {
            return Collections.emptyList();
        }

        return findAll((Specification<Job>) (root, query, cb) -> {
            Join<Job, JobSkill> jobSkillJoin = root.join("jobSkills", JoinType.LEFT);
            Join<JobSkill, Skill> skillJoin = jobSkillJoin.join("skill", JoinType.LEFT);
            //job status is active
            Predicate isActive = cb.equal(root.get("status"), JobStatus.ACTIVE);
            Predicate skillIn = skillJoin.get("skillId").in(skillIds);

            query.distinct(true);
            return cb.and(isActive, skillIn);
        });
    }
    default List<Job> getSimilarJobs(Integer jobId) {
        Job referenceJob = findById(jobId)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy công việc"));

        return findAll((Specification<Job>) (root, query, cb) -> {
            // Create joins
            Join<Job, JobCategory> categoryJoin = root.join("jobCategory", JoinType.INNER);
            Join<Job, JobLevel> levelJoin = root.join("jobLevel", JoinType.INNER);
            Join<Job, JobSkill> skillJoin = root.join("jobSkills", JoinType.LEFT);
            // Subquery for skills
            Subquery<Integer> skillSubquery = query.subquery(Integer.class);
            Root<Job> subRoot = skillSubquery.from(Job.class);
            Join<Job, JobSkill> subSkillJoin = subRoot.join("jobSkills");
            skillSubquery.select(subSkillJoin.get("skill").get("id"))
                    .where(cb.equal(subRoot.get("jobId"), jobId));
            // Count matching skills subquery
            Subquery<Long> skillCountSubquery = query.subquery(Long.class);
            Root<Job> skillCountRoot = skillCountSubquery.from(Job.class);
            Join<Job, JobSkill> countSkillJoin = skillCountRoot.join("jobSkills");
            skillCountSubquery.select(cb.count(countSkillJoin))
                    .where(
                            cb.and(
                                    cb.equal(skillCountRoot.get("jobId"), root.get("jobId")),
                                    countSkillJoin.get("skill").get("id").in(skillSubquery)
                            )
                    );

            // Main predicates
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.notEqual(root.get("jobId"), jobId)); // Not the same job
            predicates.add(cb.equal(root.get("status"), JobStatus.ACTIVE)); // Must be active
            // Must be same category
            predicates.add(cb.equal(
                    categoryJoin.get("id"),
                    referenceJob.getJobCategory().getJobCategoryId()
            ));
            if (referenceJob.getJobTitle() != null) {
                String cleanTitle = referenceJob.getJobTitle()
                        .toLowerCase()
                        .replaceAll("[\\(\\)\\[\\]\\{\\}]", " ")
                        .replaceAll("[^a-z0-9\\s]", " ")
                        .trim();

                String[] keywords = cleanTitle.split("\\s+");

                List<Predicate> titlePredicates = new ArrayList<>();
                for (String keyword : keywords) {
                    if (keyword.length() > 3) { // Bỏ qua các từ ngắn dưới 3 ký tự
                        titlePredicates.add(
                                cb.like(
                                        cb.lower(root.get("jobTitle")),
                                        "%" + keyword + "%"  // Tìm kiếm mọi title chứa keyword
                                )
                        );
                    }
                }
                // Thêm điều kiện OR - chỉ cần match một trong các keywords
                if (!titlePredicates.isEmpty()) {
                    predicates.add(cb.or(titlePredicates.toArray(new Predicate[0])));
                }
            }

//            query.distinct(true);
            query.orderBy(
                    cb.desc(skillCountSubquery),
                    cb.desc(cb.selectCase()
                            .when(cb.equal(levelJoin.get("id"),
                                    referenceJob.getJobLevel().getJobLevelId()), 1)
                            .otherwise(0)),
                    cb.desc(root.get("createdAt"))
            );
            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }
    @Query("SELECT jc.jobCategoryId, jc.jobCategoryName,COUNT(j.jobId) as jobCount " +
            "FROM JobCategory jc " +
            "LEFT JOIN Job j ON j.jobCategory.jobCategoryId = jc.jobCategoryId " +
            "AND j.status = 'ACTIVE' " +
            "AND (:month IS NULL OR MONTH(j.createdAt) = :month) " +
            "AND (:year IS NULL OR YEAR(j.createdAt) = :year) " +
            "GROUP BY jc.jobCategoryId, jc.jobCategoryName " +
            "HAVING COUNT(j.jobId) > 0 " +
            "ORDER BY jobCount DESC " +
            "LIMIT 10")
    List<Object[]> countJobsByCategory(
            @Param("month") Integer month,
            @Param("year") Integer year
    );
    Page<Job> findAllByStatusOrderByCreatedAtDesc(JobStatus status, Pageable pageable);
}
