package com.pn.career.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Table(name = "job_categories")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobCategory extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private int jobCategoryId;
    @Column(name = "category_name")
    private String jobCategoryName;
}
