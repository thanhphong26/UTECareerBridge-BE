package com.pn.career.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name="benefit_details")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BenefitDetail {
    @EmbeddedId
    private BenefitDetailId id;
    @ManyToOne
    @MapsId("employerId")
    @JoinColumn(name = "employer_id")
    private Employer employer;

    @ManyToOne
    @MapsId("benefitId")
    @JoinColumn(name = "benefit_id")
    private Benefit benefit;
    @Column(name = "description")
    private String description;
}
