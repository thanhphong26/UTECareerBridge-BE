package com.pn.career.models;

import com.pn.career.converters.JpaConverterFlexible;
import com.pn.career.converters.JpaConverterJson;
import com.pn.career.converters.JpaConverterList;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;

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
    @Convert(converter = JpaConverterFlexible.class)
    @Column(name = "theme")
    private Object theme;

    @Convert(converter = JpaConverterFlexible.class)
    @Column(name = "personal_info")
    private Object personalInfo;

    @Convert(converter = JpaConverterFlexible.class)
    @Column(name = "sections")
    private Object sections;

    @Convert(converter = JpaConverterFlexible.class)
    @Column(name = "work_experiences")
    private Object workExperience;

    @Convert(converter = JpaConverterFlexible.class)
    @Column(name = "certificates")
    private Object certificates;
}
