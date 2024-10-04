package com.pn.career.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PackageDTO {
    private String packageName;
    private float price;
    private String description;
    private int duration;
    private int amount;
    private int featureId;
    private boolean isActive;
}
