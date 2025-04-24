package com.pn.career.repositories;

import com.pn.career.models.JobSkill;
import com.pn.career.models.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Integer> {
    List<Skill> findBySkillNameIn(List<String> skillNames);
    List<Skill> findAllByIsActiveTrue();
    boolean existsBySkillName(String skillName);
}
