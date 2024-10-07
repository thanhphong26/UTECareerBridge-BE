package com.pn.career.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Table(name = "resumes")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Resume extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resume_id")
    private int resumeId;
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    @Column(name = "resume_title")
    private String resumeTitle;
    @Column(name = "resume_file")
    private String resumeFile;
    @Column(name = "resume_description")
    private String resumeDescription;
    @ManyToOne
    @JoinColumn(name="level_id")
    private JobLevel jobLevel;
    @Column(name = "is_active")
    private boolean isActive;
    @OneToMany(mappedBy = "resume")
    private List<Application> applications;
}
