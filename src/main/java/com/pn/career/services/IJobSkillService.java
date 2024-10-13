package com.pn.career.services;

import com.pn.career.models.Job;
import java.util.Set;

public interface IJobSkillService {
    void createJobSkill(Job job, Set<Integer> skillIds);
    void updateJobSkill(Job job, Set<Integer> newSkillIds);
}
