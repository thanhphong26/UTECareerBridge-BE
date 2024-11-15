package com.pn.career.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RevenueByMonth {
    private Integer month;
    private Integer year;
    private Long revenue;
    private Integer numberOfPackages;
}
