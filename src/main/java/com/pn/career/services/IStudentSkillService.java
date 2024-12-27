package com.pn.career.services;

import com.pn.career.models.Job;
import com.pn.career.models.StudentSkill;

import java.util.List;

public interface IStudentSkillService {
        StudentSkill createStudentSkill(Integer studentId, Integer skillId, Integer level);
        List<StudentSkill> getStudentSkills(Integer studentId);
        void deleteStudentSkill(Integer studentId, Integer skillId);
        List<Job> getJobsBySkill(Integer skillId);
}
