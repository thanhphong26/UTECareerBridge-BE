package com.pn.career.models;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class EmployerPackageId implements Serializable {
    private Integer employerId;
    private Integer packageId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmployerPackageId that = (EmployerPackageId) o;

        if (employerId != that.employerId) return false;
        return packageId == that.packageId;
    }

    @Override
    public int hashCode() {
        int result = employerId;
        result = 31 * result + packageId;
        return result;
    }
}
