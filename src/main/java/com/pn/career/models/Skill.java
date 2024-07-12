package com.pn.career.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Table(name = "skills")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Skill extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skill_id")
    private int skillId;
    @Column(name = "skill_name")
    private String skillName;
    @Column(name = "skill_level")
    private String skillLevel;
}
