package com.pn.career.repositories;

import com.pn.career.models.Job;
import com.pn.career.models.JobSkill;
import com.pn.career.models.JobSkillId;
import com.pn.career.models.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface JobSkillRepository extends JpaRepository<JobSkill, JobSkillId> {
    List<JobSkill> findAllBySkill(Skill skill);
    List<JobSkill> findAllByJob(Job job);
    @Modifying
    @Query("DELETE FROM JobSkill js WHERE js.job = :job AND js.skill.skillId IN :skillIds")
    void deleteByJobAndSkillIdIn(@Param("job") Job job, @Param("skillIds") Set<Integer> skillIds);

}
