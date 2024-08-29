package com.pn.career.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Table(name="benefits")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Benefit extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="benefit_id")
    private int benefitId;
    @Column(name="benefit_name")
    private String benefitName;
    @Column(name="benefit_icon")
    private String benefitIcon;
    @Column(name="is_active")
    private boolean isActive;
}
