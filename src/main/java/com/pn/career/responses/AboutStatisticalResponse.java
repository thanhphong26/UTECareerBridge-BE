package com.pn.career.responses;

import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AboutStatisticalResponse {
    private Integer eventsByYear;
    private Integer countEmployers;
    private Integer countJob;
    public static AboutStatisticalResponse fromStatistical(Integer eventsByYear, Integer countEmployers, Integer countJob) {
        return AboutStatisticalResponse.builder()
                .eventsByYear(eventsByYear)
                .countEmployers(countEmployers)
                .countJob(countJob)
                .build();
    }
}
