package com.pn.career.responses;

import com.pn.career.dtos.EmployerJobCountDTO;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopEmployerResponse {
    private EmployerResponse employerResponse;
    private Long countJob;
    public static TopEmployerResponse fromEmployerJobCount(EmployerJobCountDTO employer) {
        return TopEmployerResponse.builder()
                .employerResponse(employer.getEmployer() != null ? EmployerResponse.fromUser(employer.getEmployer()) : null)
                .countJob(employer.getCountJob())
                .build();
    }
}
