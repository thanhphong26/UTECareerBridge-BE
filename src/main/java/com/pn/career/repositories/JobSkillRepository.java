package com.pn.career.repositories;

import com.pn.career.models.JobSkill;
import com.pn.career.models.JobSkillId;
import com.pn.career.models.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobSkillRepository extends JpaRepository<JobSkill, JobSkillId> {
    List<JobSkill> findAllBySkill(Skill skill);
}
