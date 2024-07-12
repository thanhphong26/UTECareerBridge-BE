package com.pn.career.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "applications")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Application extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private int applicationId;
    @ManyToOne
    @JoinColumn(name = "job_id",nullable = false)
    private Job job;
    @ManyToOne
    @JoinColumn(name = "student_id",nullable = false)
    private Student student;
    @Enumerated(EnumType.STRING)
    @Column(name = "application_status", nullable = false)
    private ApplicationStatus applicationStatus = ApplicationStatus.PENDING;
    @Column(name = "application_date")
    private LocalDateTime applicationDate;
    @OneToOne(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private Interview interview;
}
