package com.pn.career.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Table(name="benefit_details")
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BenefitDetail {
    @EmbeddedId
    private BenefitDetailId id;
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("employerId")
    @JoinColumn(name = "employer_id")
    @JsonBackReference
    private Employer employer;

    @ManyToOne
    @MapsId("benefitId")
    @JoinColumn(name = "benefit_id")
    private Benefit benefit;
    @Column(name = "description")
    private String description;
}
