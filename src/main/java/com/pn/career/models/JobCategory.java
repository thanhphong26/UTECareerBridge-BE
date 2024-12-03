package com.pn.career.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Table(name = "job_categories")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class JobCategory extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private int jobCategoryId;
    @Column(name = "category_name", unique = true)
    private String jobCategoryName;
    @Column(name = "is_active")
    private boolean isActive;
    @JsonBackReference
    @OneToMany(mappedBy = "jobCategory", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Student> students;
}
