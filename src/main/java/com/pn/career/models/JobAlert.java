package com.pn.career.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "job_alerts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class JobAlert extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "job_title")
    private String jobTitle;
    @Column(name = "min_salary")
    private Double minSalary;
    private String level;
    private String location;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private JobCategory jobCategory;
    @Column(name = "company_field")
    private String companyField;
    @Enumerated(EnumType.STRING)
    private FrequencyEnum frequency;
    @Column(name = "notify_by_email")
    private boolean notifyByEmail;
    @Column(name = "notify_by_app")
    private boolean notifyByApp;
    private boolean active;
}
