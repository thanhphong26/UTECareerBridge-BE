package com.pn.career.responses;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployerListResponse {
    List<EmployerResponse> employerResponses;
    int totalPages;
}
