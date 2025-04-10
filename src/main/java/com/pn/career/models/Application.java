package com.pn.career.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Table(name = "applications")
@Entity
@Getter
@Setter
@ToString(exclude = {"resume", "job"})
@EqualsAndHashCode(exclude = {"resume", "job"})
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Application extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private int applicationId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "job_id",nullable = false)
    private Job job;
    @Enumerated(EnumType.STRING)
    @Column(name = "application_status", nullable = false)
    private ApplicationStatus applicationStatus = ApplicationStatus.PENDING;

}
