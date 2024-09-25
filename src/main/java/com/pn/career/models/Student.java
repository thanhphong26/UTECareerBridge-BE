package com.pn.career.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Table(name = "students")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@PrimaryKeyJoinColumn(name = "student_id")
public class Student extends User{
    @Column(name = "university_email")
    private String universityEmail;
    @Column(name="year")
    private int year;
    @Column(name="profile_image")
    private String profileImage;
    @Column(name = "is_find")
    private boolean isFind;
    @OneToMany(mappedBy = "student")
    private Set<Resume> resumes;
}
