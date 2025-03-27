package com.pn.career.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "student_cvs")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class StudentCV extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    @ManyToOne
    @JoinColumn(name = "cv_template_id", nullable = false)
    private CVTemplate cvTemplate;
    @Column(name = "cv_content", columnDefinition = "TEXT")
    private String cvContent;
    @Column(name = "is_active", nullable = false)
    private boolean isActive;
}
