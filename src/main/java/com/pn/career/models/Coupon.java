package com.pn.career.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;

@Table(name = "coupons")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Coupon extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private int couponId;
    @Column(name = "coupon_code", nullable = false, unique = true)
    private String couponCode;
    private int amount;
    private float discount;
    @Column(name = "expired_at")
    private LocalDateTime expiredAt;
    private String description;
    @Column(name = "max_usage")
    private int maxUsage;
    @Column(name = "is_active")
    private boolean isActive;
}
