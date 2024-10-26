package com.pn.career.repositories;

import com.pn.career.models.*;
import com.pn.career.responses.StudentResponse;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.stream.Collectors;

public interface StudentRepository extends JpaRepository<Student, Integer>, JpaSpecificationExecutor<Student> {
    List<Student> findAllByRole_RoleName(String role);
    default List<StudentResponse> findAllStudentsByApplication(Integer employerId) {
        Specification<Student> spec = (root, query, criteriaBuilder) -> {
            Join<Student, Resume> resumeJoin = root.join("resumes", JoinType.INNER);
            Join<Resume, Application> applicationJoin = resumeJoin.join("applications", JoinType.INNER);
            Join<Application, Job> jobJoin = applicationJoin.join("job", JoinType.INNER);
            Join<Job, Employer> employerJoin = jobJoin.join("employer", JoinType.INNER);

            query.distinct(true);

            query.multiselect(
                    root.get("id"),
                    root.get("firstName"),
                    root.get("lastName"),
                    root.get("email"),
                    applicationJoin.get("applicationId"),
                    applicationJoin.get("applicationStatus"),
                    jobJoin.get("jobTitle")
            );

            return criteriaBuilder.equal(employerJoin.get("id"), employerId);
        };
        return findAll(spec).stream().map(StudentResponse::fromStudent).collect(Collectors.toList());
    }
}
