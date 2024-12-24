package com.pn.career.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "employer_packages")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class EmployerPackage {
    @EmbeddedId
    private EmployerPackageId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("employerId")
    @JoinColumn(name = "employer_id")
    private Employer employer;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("packageId")
    @JoinColumn(name = "package_id")
    private Package jobPackage;
    private int amount;
    @Column(name = "expired_at")
    private LocalDateTime expiredAt;
}
