package com.pn.career.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Table(name = "jobs")
@Getter
@Setter
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
    @Column(name = "job_min_salary", precision = 18, scale = 2)
    private BigDecimal jobMinSalary;
    @Column(name = "job_max_salary", precision = 18, scale = 2)
    private BigDecimal jobMaxSalary;
    @Column(name = "job_deadline")
    private LocalDate jobDeadline;
    @Column(name="amount")
    private int amount;
    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private JobStatus status;
    @Column(name = "rejection_reason")
    private String rejectionReason;
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Application> applications;
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // hoặc @JsonIgnore nếu không cần
    private List<JobSkill> jobSkills=new ArrayList<>();
    @Column(name = "package_id")
    private int packageId;
}
