package com.pn.career.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;

@Table(name = "students", indexes = {
        @Index(name = "idx_student_category", columnList = "category_id"),
        @Index(name = "idx_student_uni_email", columnList = "university_email")
})
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@SuperBuilder
@PrimaryKeyJoinColumn(name = "student_id")
public class Student extends User{
    @Column(name = "university_email")
    private String universityEmail;
    @Column(name="year")
    private Integer year;
    @Column(name="profile_image")
    private String profileImage;
    @Column(name = "is_find")
    private boolean isFind;
    @OneToMany(mappedBy = "student")
    private Set<Resume> resumes;
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StudentSkill> studentSkills;
    @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "category_id", nullable = true)
    private JobCategory jobCategory;

}
