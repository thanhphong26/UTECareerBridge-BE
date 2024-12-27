package com.pn.career.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@Table(name = "package_usage")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackageUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "employer_id", referencedColumnName = "employer_id"),
            @JoinColumn(name = "package_id", referencedColumnName = "package_id")
    })
    private EmployerPackage employerPackage;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    private Job job;

    @Column(name = "used_amount")
    private int usedAmount;

    @Column(name = "usage_date")
    private LocalDateTime usageDate;
}
