package com.pn.career.repositories;

import com.pn.career.models.Order;
import com.pn.career.models.PaymentStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer>, JpaSpecificationExecutor<Order> {
    Page<Order> findAllByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    Page<Order> findByEmployer_UserId(Integer employerId, Pageable pageable);
    List<Order> findByOrderDateBetweenAndPaymentStatus(LocalDateTime startDate, LocalDateTime endDate, PaymentStatus paymentStatus);
    default Page<Order> search(String keyword, Integer employerId, LocalDate startDate, LocalDate endDate, PaymentStatus paymentStatus, Pageable pageable) {
        return findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isEmpty()) {
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("accountNumber")), "%" + keyword + "%"),
                        cb.like(cb.lower(root.get("paymentStatus")), "%" + keyword + "%")
                ));
            }
            if (employerId != null && employerId != 0) {
                predicates.add(cb.equal(root.get("employer").get("userId"), employerId));
            }
            if (startDate != null && endDate != null) {
                predicates.add(cb.between(root.get("orderDate"), startDate, endDate));
            }
            if (paymentStatus != null) {
                predicates.add(cb.equal(root.get("paymentStatus"), paymentStatus));
            }
            query.distinct(true);
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }
    @Query("SELECT MONTH(o.paymentDate) AS month, " +
            "COUNT(od.jobPackage.packageId) AS packageCount, " +
            "SUM(o.total) AS totalRevenue " +
            "FROM Order o " +
            "JOIN o.orderDetails od " +
            "WHERE YEAR(o.paymentDate) = :year " +
            "AND o.paymentStatus = 'PAID' " +
            "GROUP BY MONTH(o.paymentDate) " +
            "ORDER BY MONTH(o.paymentDate)")
    List<Object[]> getRevenueByMonth(@Param("year") Integer year);
}

