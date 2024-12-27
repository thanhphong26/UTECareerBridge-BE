package com.pn.career.responses;

import com.pn.career.models.Coupon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CouponListResponse {
    List<Coupon> couponList;
    int totalPages;
}
