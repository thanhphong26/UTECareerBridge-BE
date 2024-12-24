package com.pn.career.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Table(name = "job_levels")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class JobLevel extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "level_id")
    private int jobLevelId;
    @Column(name = "name_level")
    private String nameLevel;
    @Column(name = "is_active")
    private boolean isActive;
}
