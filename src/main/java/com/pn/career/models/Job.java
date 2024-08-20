package com.pn.career.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Table(name = "jobs")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Job extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    private int jobId;
    @ManyToOne
    @JoinColumn(name = "employer_id")
    private Employer employer;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private JobCategory jobCategory;
    @ManyToOne
    @JoinColumn(name = "level_id")
    private JobLevel jobLevel;
    @Column(name = "job_title")
    private String jobTitle;
    @Column(name = "job_description")
    private String jobDescription;
    @Column(name = "job_requirements")
    private String jobRequirements;
    @Column(name = "job_location")
    private String jobLocation;
    @Column(name = "job_salary", precision = 18, scale = 2)
    private BigDecimal jobSalary;
    @Column(name = "job_deadline")
    private LocalDate jobDeadline;
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Application> applications;
}
