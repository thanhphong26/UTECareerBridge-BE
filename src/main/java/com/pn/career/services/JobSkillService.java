package com.pn.career.services;

import com.pn.career.models.Job;
import com.pn.career.models.JobSkill;
import com.pn.career.models.JobSkillId;
import com.pn.career.models.Skill;
import com.pn.career.repositories.JobSkillRepository;
import com.pn.career.repositories.SkillRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class JobSkillService implements IJobSkillService{
    private final JobSkillRepository jobSkillRepository;
    private final SkillRepository skillRepository;

    @Override
    @Transactional
    public void createJobSkill(Job job, Set<Integer> skillIds) {
        Set<JobSkill> jobSkills = skillIds.stream()
                .map(skillId -> {
                    Skill skill=skillRepository.findById(skillId).orElseThrow(()->new RuntimeException("Không tìm thấy kỹ năng phù hợp"));
                    return JobSkill.builder()
                            .id(JobSkillId.builder()
                                    .jobId(job.getJobId())
                                    .skillId(skillId)
                                    .build())
                            .job(job)
                            .skill(skill)
                            .build();
                })
                .collect(Collectors.toSet());
        jobSkillRepository.saveAll(jobSkills);
    }

    @Override
    @Transactional
    public void updateJobSkill(Job job, Set<Integer> newSkillIds) {
        // Lấy tất cả JobSkill hiện tại của job
        List<JobSkill> currentJobSkills = jobSkillRepository.findAllByJob(job);
        Set<Integer> currentSkillIds = currentJobSkills.stream()
                .map(jobSkill -> jobSkill.getSkill().getSkillId())
                .collect(Collectors.toSet());
        Set<Integer> skillIdsToAdd = new HashSet<>(newSkillIds);
        skillIdsToAdd.removeAll(currentSkillIds);
        // Xác định các skill cần xóa
        Set<Integer> skillIdsToRemove = new HashSet<>(currentSkillIds);
        skillIdsToRemove.removeAll(newSkillIds);
        // Xóa các JobSkill không còn cần thiết
        if (!skillIdsToRemove.isEmpty()) {
            jobSkillRepository.deleteByJobAndSkillIdIn(job, skillIdsToRemove);
        }
        // Thêm các JobSkill mới
        if (!skillIdsToAdd.isEmpty()) {
            createJobSkill(job, skillIdsToAdd);
        }
    }
}
