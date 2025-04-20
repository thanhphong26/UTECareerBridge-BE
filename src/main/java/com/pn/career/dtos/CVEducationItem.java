package com.pn.career.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CVEducationItem {
    private String school;
    private String degree;
    private String startYear;
    private String endYear;
    private String year;
}
