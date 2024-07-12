package com.pn.career.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Table(name = "students")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@PrimaryKeyJoinColumn(name = "student_id")
public class Student extends User{
    @Column(name = "university_mail")
    private String universityMail;
    @Column(name="profile_image")
    private String profileImage;
    private String address;
    @OneToMany(mappedBy = "student")
    private Set<Application> applications;
}
