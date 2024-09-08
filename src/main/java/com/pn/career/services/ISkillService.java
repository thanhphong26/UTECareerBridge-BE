package com.pn.career.services;

import com.pn.career.models.Skill;

import java.util.List;

public interface ISkillService {
    List<Skill> findAllSkills(boolean isAdmin);
}
