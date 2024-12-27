package com.pn.career.services;

import com.pn.career.dtos.CouponDTO;
import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.exceptions.DuplicateNameException;
import com.pn.career.models.Coupon;
import com.pn.career.repositories.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponService implements ICouponService{
    private final CouponRepository couponRepository;

    @Override
    public Coupon getCouponById(Integer couponId) {
        return couponRepository.findById(couponId).orElseThrow(()->new DataNotFoundException("Không tim thấy mã giảm giá"));
    }

    @Override
    public Page<Coupon> getAllCoupons(boolean isAdmin, PageRequest pageRequest) {
        if(isAdmin){
            return couponRepository.findAll(pageRequest);
        }
        return couponRepository.findAllByIsActive(true, pageRequest);
    }

    @Override
    public Coupon createCoupon(CouponDTO couponDTO) {
        if(couponRepository.existsByCouponCode(couponDTO.getCouponCode())){
            throw new DuplicateNameException("Mã giảm giá đã tồn tại");
        }
        Coupon coupon=Coupon.builder()
                .couponCode(couponDTO.getCouponCode())
                .discount(couponDTO.getDiscount())
                .amount(couponDTO.getAmount())
                .expiredAt(couponDTO.getExpiredAt())
                .description(couponDTO.getDescription())
                .maxUsage(couponDTO.getMaxUsage())
                .isActive(true)
                .build();
        return couponRepository.save(coupon);
    }

    @Override
    public Coupon updateCoupon(Integer couponId, CouponDTO coupon) {
        Coupon existingCoupon=couponRepository.findById(couponId).orElseThrow(()->new DataNotFoundException("Không tim thấy mã giảm giá"));
        //validate coupon code
        if(!existingCoupon.getCouponCode().equals(coupon.getCouponCode())){
            if(couponRepository.existsByCouponCode(coupon.getCouponCode())){
                throw new DuplicateNameException("Mã giảm giá đã tồn tại");
            }
            existingCoupon.setCouponCode(coupon.getCouponCode());
        }
        existingCoupon.setAmount(coupon.getAmount());
        existingCoupon.setDiscount(coupon.getDiscount());
        existingCoupon.setExpiredAt(coupon.getExpiredAt());
        existingCoupon.setDescription(coupon.getDescription());
        existingCoupon.setMaxUsage(coupon.getMaxUsage());
        existingCoupon.setActive(coupon.isActive());
        return couponRepository.save(existingCoupon);
    }
    @Override
    public void deleteCoupon(Integer couponId) {
        Coupon existingCoupon=couponRepository.findById(couponId).orElseThrow(()->new DataNotFoundException("Không tim thấy mã giảm giá"));
        couponRepository.delete(existingCoupon);
    }
}
