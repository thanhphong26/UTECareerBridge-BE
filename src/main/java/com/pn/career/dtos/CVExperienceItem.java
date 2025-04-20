package com.pn.career.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CVExperienceItem {
    private String company;
    private String title;
    private String startDate;
    private String endDate;
}