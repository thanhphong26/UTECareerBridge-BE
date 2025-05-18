package com.pn.career.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserGrowthResponse {
    private Integer year;
    private Integer month;
    private Integer employerCount;
    private Integer studentCount;
}
