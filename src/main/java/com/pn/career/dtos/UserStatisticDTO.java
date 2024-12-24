package com.pn.career.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatisticDTO {
    private Integer totalEmployers;
    private Integer totalCandidates;
}
