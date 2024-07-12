package com.pn.career.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Table(name = "coupons")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Coupon extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private int couponId;
    @Column(name = "coupon_code")
    private String couponCode;
    private int amount;
    private float discount;
    @Column(name = "is_expired")
    private boolean isExpired;
    private String description;
    @Column(name = "max_usage")
    private int maxUsage;
    @Column(name = "is_active")
    private boolean isActive;
}
