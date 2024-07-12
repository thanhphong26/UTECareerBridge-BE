package com.pn.career.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.bytecode.enhance.spi.EnhancementInfo;

import java.time.LocalDate;
import java.util.Set;

@Table(name = "jobs")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    @Column(name = "job_requirement")
    private String jobRequirement;
    @Column(name = "job_salary")
    private String jobSalary;
    @Column(name = "job_location")
    private String jobLocation;
    @Column(name = "job_deadline")
    private LocalDate jobDeadline;
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Application> applications;
}
