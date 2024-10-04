package com.pn.career.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CouponDTO {
    private String couponCode;
    private Integer amount;
    private float discount;
    private LocalDateTime expiredAt;
    private String description;
    private Integer maxUsage;
    private boolean active;
}
