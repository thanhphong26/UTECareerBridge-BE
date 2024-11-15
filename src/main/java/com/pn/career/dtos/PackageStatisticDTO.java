package com.pn.career.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackageStatisticDTO {
    private Integer packageId;
    private String packageName;
    private Long packageCount;
}
