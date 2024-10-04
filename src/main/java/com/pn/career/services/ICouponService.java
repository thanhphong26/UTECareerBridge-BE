package com.pn.career.services;

import com.pn.career.dtos.CouponDTO;
import com.pn.career.models.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ICouponService {
    Coupon getCouponById(Integer couponId);
    Page<Coupon> getAllCoupons(boolean isAdmin, PageRequest pageRequest);
    Coupon createCoupon(CouponDTO couponDTO);
    Coupon updateCoupon(Integer couponId, CouponDTO couponDTO);
    void deleteCoupon(Integer couponId);
}
