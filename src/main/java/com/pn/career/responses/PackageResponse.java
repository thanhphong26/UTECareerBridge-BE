package com.pn.career.responses;

import com.pn.career.models.Package;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PackageResponse {
    private Integer packageId;
    private String packageName;
    private float price;
    private String description;
    private int duration;
    private int amount;
    private Integer featureId;
    private String featureName;
    private String featureDescription;
    public static PackageResponse from(Package package1) {
        return PackageResponse.builder()
                .packageId(package1.getPackageId())
                .packageName(package1.getPackageName())
                .price(package1.getPrice())
                .description(package1.getDescription())
                .duration(package1.getDuration())
                .amount(package1.getAmount())
                .featureId(package1.getFeature().getFeatureId())
                .featureName(package1.getFeature().getFeatureName())
                .featureDescription(package1.getFeature().getDescription())
                .build();
    }
}
