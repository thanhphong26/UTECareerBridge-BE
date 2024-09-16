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
public class BenefitDetailId implements Serializable {
    private Integer employerId;
    private Integer benefitId;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BenefitDetailId that = (BenefitDetailId) o;
        return Objects.equals(benefitId, that.benefitId) &&
                Objects.equals(employerId, that.employerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(benefitId, employerId);
    }
}
