package com.pn.career.models;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
@Data
@Embeddable
public class EmployerPackageId implements Serializable {
    private int employerId;
    private int packageId;

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
