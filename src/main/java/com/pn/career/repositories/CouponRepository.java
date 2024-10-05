package com.pn.career.repositories;

import com.pn.career.models.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CouponRepository extends JpaRepository<Coupon, Integer> {
    Page<Coupon> findAllByIsActive(Boolean isActive, Pageable pageable);
    Coupon findByCouponCode(String couponCode);
    boolean existsByCouponCode (String couponCode);
}
