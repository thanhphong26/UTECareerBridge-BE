package com.pn.career.services;

import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.models.*;
import com.pn.career.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentSkillService implements IStudentSkillService{
    private final StudentSkillRepository studentSkillRepository;
    private final JobRepository jobRepository;
    private final StudentRepository studentRepository;
    private final SkillRepository skillRepository;
    @Override
    public StudentSkill createStudentSkill(Integer studentId, Integer skillId, Integer level) {
        Student student=studentRepository.findById(studentId).orElseThrow(()->
                new DataNotFoundException("Không tìm thấy student tương ứng"));
        Skill skill=skillRepository.findById(skillId).orElseThrow(()->
                new DataNotFoundException("Không tìm thấy skill tương ứng"));
        StudentSkill studentSkill=StudentSkill.builder()
                .id(StudentSkillId.builder()
                        .studentId(studentId)
                        .skillId(skillId)
                        .build())
                .student(student)
                .skill(skill)
                .level(level)
                .build();
        return studentSkillRepository.save(studentSkill);
    }
    @Override
    public List<StudentSkill> getStudentSkills(Integer studentId) {
        return studentSkillRepository.findAllByStudent_UserId(studentId);
    }

    @Override
    @Transactional
    public void deleteStudentSkill(Integer studentId, Integer skillId) {
        studentSkillRepository.deleteByStudent_UserIdAndSkill_SkillId(studentId, skillId);
    }

    @Override
    public List<Job> getJobsBySkill(Integer skillId) {
        return jobRepository.findAllByJobSkills_SkillId(skillId);
    }
    public List<Job> getJobsByStudentSkill(Integer studentId) {
        List<Integer> studentSkillIds = studentSkillRepository.findAllByStudent_UserId(studentId).stream()
                .map(studentSkill -> studentSkill.getSkill().getSkillId())
                .filter(skillId -> skillId != null)
                .toList();

        if (studentSkillIds.isEmpty()) {
            return List.of(); // Return an empty list if the student has no skills
        }

        return jobRepository.findAllByJobSkillsAndJobSkillsIn(studentSkillIds);
    }
}
