package com.pn.career.dtos;

import com.pn.career.models.Employer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployerJobCountDTO {
    private Employer employer;
    private Long countJob;
}
