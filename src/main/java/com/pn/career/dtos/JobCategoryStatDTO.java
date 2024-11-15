package com.pn.career.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobCategoryStatDTO {
    private Integer categoryId;
    private String categoryName;
    private Long jobCount;
}