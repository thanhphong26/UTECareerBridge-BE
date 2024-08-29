package com.pn.career.models;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class BenefitDetailId {
    private Integer employerId;
    private Integer benefitId;
}
