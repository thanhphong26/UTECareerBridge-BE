package com.pn.career.models;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;


@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BenefitDetailId {
    private Integer employerId;
    private Integer benefitId;
}
