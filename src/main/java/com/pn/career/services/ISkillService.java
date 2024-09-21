package com.pn.career.services;

import com.pn.career.dtos.SkillDTO;
import com.pn.career.dtos.SkillUpdateDTO;
import com.pn.career.models.Skill;

import java.util.List;

public interface ISkillService {
    List<Skill> findAllSkills(boolean isAdmin);
    Skill createSkill(SkillDTO skill);
    Skill getSkillById(Integer skillId);
    Skill updateSkill(Integer skillId, SkillUpdateDTO skill) ;
    Skill deleteSkill(Integer skillId) throws Exception;
}
