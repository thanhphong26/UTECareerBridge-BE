package com.pn.career.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Table(name = "packages")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Package extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "package_id")
    private int packageId;
    @Column(name = "package_name", nullable = false)
    private String packageName;
    private float price;
    private String description;
    private int duration;
    private  int amount;
    @ManyToOne
    @JoinColumn(name = "feature_id",nullable = false)
    private Feature feature;
    @Column(name = "is_active")
    private boolean isActive;
    @OneToMany(mappedBy = "jobPackage", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EmployerPackage> employerPackages;
}
