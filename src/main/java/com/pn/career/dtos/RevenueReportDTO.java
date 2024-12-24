package com.pn.career.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RevenueReportDTO {
    private BigDecimal totalRevenue;
    private long orderCount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal averageOrderValue;
    private BigDecimal dailyAverage;
}
