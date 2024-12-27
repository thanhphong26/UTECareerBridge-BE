package com.pn.career.repositories;

import com.pn.career.dtos.UserStatisticDTO;
import com.pn.career.models.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    User findUserByRole_RoleName(String roleName);
    Optional<User> findUserByEmail(String email);
    Optional<User> findUserByPhoneNumber(String phoneNumber);
    Optional<User> findUserByEmailOrPhoneNumber(String email, String phoneNumber);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    default Page<User> getAllUsersByRole(String keyword, String role, String sorting, Pageable pageable){
        Specification<User> spec = (root, query, criteriaBuilder) -> {
            query.distinct(true);
            query.multiselect(
                    root.get("id"),
                    root.get("firstName"),
                    root.get("lastName"),
                    root.get("email"),
                    root.get("phoneNumber"),
                    root.get("active")
            );
            Predicate rolePredicate = criteriaBuilder.equal(root.get("role").get("roleName"), role);
            Predicate keywordPredicate = criteriaBuilder.or(
                    criteriaBuilder.like(root.get("firstName"), "%" + keyword + "%"),
                    criteriaBuilder.like(root.get("lastName"), "%" + keyword + "%"),
                    criteriaBuilder.like(root.get("email"), "%" + keyword + "%"),
                    criteriaBuilder.like(root.get("phoneNumber"), "%" + keyword + "%"),
                    criteriaBuilder.like(root.get("address"), "%" + keyword + "%")
            );
            // Xử lý ORDER BY
            if (query.getResultType().equals(User.class)) {
                switch (sorting) {
                    case "lastest":
                        query.orderBy(criteriaBuilder.asc(root.get("createdAt")));
                        break;
                    case "newest":
                        query.orderBy(criteriaBuilder.desc(root.get("createdAt")));
                        break;
                    case "ascName":
                        query.orderBy(criteriaBuilder.asc(root.get("firstName")));
                        break;
                    case "descName":
                        query.orderBy(criteriaBuilder.desc(root.get("firstName")));
                        break;
                    default:
                        query.orderBy(criteriaBuilder.desc(root.get("createdAt")));
                        break;
                }
            }

            return criteriaBuilder.and(rolePredicate, keywordPredicate);
        };

        return findAll(spec, pageable);
    }
    //get all user , user by role
    @Query("SELECT new map(" +
            "SUM(CASE WHEN u.role.roleName = 'employer' THEN 1 ELSE 0 END) as totalEmployers, " +
            "SUM(CASE WHEN u.role.roleName = 'student' THEN 1 ELSE 0 END) as totalCandidates) " +
            "FROM User u")
    Map<String, Object> countUsersByRole();

}
