package com.pn.career.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Table(name = "features")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Feature extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feature_id")
    private int featureId;
    @Column(name = "feature_name")
    private String featureName;
    @Column(name = "description")
    private String description;
    @Column(name = "is_active")
    private boolean isActive;
    @OneToMany(mappedBy = "feature", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Package> packages;
}
