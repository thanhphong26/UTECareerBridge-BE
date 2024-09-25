package com.pn.career.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Table(name = "applications")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Application extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private int applicationId;
    @ManyToOne
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;
    @ManyToOne
    @JoinColumn(name = "job_id",nullable = false)
    private Job job;
    @Enumerated(EnumType.STRING)
    @Column(name = "application_status", nullable = false)
    private ApplicationStatus applicationStatus = ApplicationStatus.PENDING;
    @OneToOne(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private Interview interview;
}
