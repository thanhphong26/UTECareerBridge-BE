package com.pn.career.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Table(name = "job_levels")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobLevel extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "level_id")
    private int jobLevelId;
    @Column(name = "name_level")
    private String nameLevel;
}
