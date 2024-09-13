package com.pn.career.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Table(name = "job_categories")
@Entity
@Data
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
}
