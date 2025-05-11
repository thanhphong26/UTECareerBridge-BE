package com.pn.career.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployerDashboardResponse {
    private Integer totalJobs;
    private Integer totalApplications;
    private Integer totalInterviews;
    private Integer totalHired;
    private Integer totalRejected;
    private Integer totalPending;
    private Integer totalResumes;
    private Double timeAverageToHire;
}
