package com.pn.career.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data//toString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryJobUpdateDTO {
    private String categoryJobName;
    private boolean active;
}
