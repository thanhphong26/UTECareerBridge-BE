package com.pn.career.responses;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminJobResponse {
    private Integer activeJob;
    private Integer pendingJob;
    private Integer rejectedJob;
    private Integer countInterview;
    private Integer successfulInterview;
}
