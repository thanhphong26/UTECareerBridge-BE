package com.pn.career.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminTopEmployerResponse {
    private Integer employerId;
    private String name;
    private Integer jobPosted;
    private Integer hires;
}
