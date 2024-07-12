package com.pn.career.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Table(name = "resumes")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Resume extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resume_id")
    private int resumeId;
    @OneToMany
    @JoinColumn(name = "student_id")
    private Student student;
    @Column(name = "resume_title")
    private String resumeTitle;
    @Column(name = "resume_file")
    private String resumeFile;
    @Column(name = "resume_description")
    private String resumeDescription;
}
