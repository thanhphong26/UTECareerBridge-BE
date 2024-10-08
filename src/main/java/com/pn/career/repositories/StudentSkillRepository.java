package com.pn.career.repositories;
import com.pn.career.models.Skill;
import com.pn.career.models.StudentSkill;
import com.pn.career.models.StudentSkillId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentSkillRepository extends JpaRepository<StudentSkill, StudentSkillId> {
    List<StudentSkill> findAllBySkill(Skill skill);
    List<StudentSkill> findAllByStudent_UserId(Integer studentId);
}
