package com.pn.career.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecruitmentPerformanceResponse {
    private Integer jobId;
    private String title;
    private Integer views;
    private Integer applications;
}
