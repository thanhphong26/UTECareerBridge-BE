package com.pn.career.services;

import com.pn.career.models.Job;
import com.pn.career.models.JobSkill;
import com.pn.career.models.JobSkillId;
import com.pn.career.models.Skill;
import com.pn.career.repositories.JobRepository;
import com.pn.career.repositories.JobSkillRepository;
import com.pn.career.repositories.SkillRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class JobSkillService implements IJobSkillService{
    private final JobSkillRepository jobSkillRepository;
    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;

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

    @Override
    @Transactional
    public List<Job> getRecommendedJobs(String skills, int limit) {
        if (skills == null || skills.trim().isEmpty()) {
            // If no skills provided, return latest jobs
            return jobRepository.findTop5ByOrderByCreatedAtDesc();
        }
        // If skills are provided, find jobs that match those skills
        log.info("Finding recommended jobs for skills: {}", skills);
        // Parse the skills string - assuming skills are comma-separated
        List<String> skillNames = Arrays.stream(skills.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        if (skillNames.isEmpty()) {
            return jobRepository.findTop5ByOrderByCreatedAtDesc();
        }
        log.info("Finding recommended jobs for skills: {}", skillNames);
        // Find skills in the database
        List<Skill> userSkills = skillRepository.findBySkillNameIn(skillNames);

        if (userSkills.isEmpty()) {
            return jobRepository.findTop5ByOrderByCreatedAtDesc();
        }

        // Get all job skills that match user skills
        Map<Job, Integer> jobMatchCount = new HashMap<>();

        for (Skill skill : userSkills) {
            List<JobSkill> matchingJobSkills = jobSkillRepository.findAllBySkill(skill);
            for (JobSkill js : matchingJobSkills) {
                Job job = js.getJob();
                jobMatchCount.put(job, jobMatchCount.getOrDefault(job, 0) + 1);
            }
        }

        // Sort jobs by number of matching skills (descending)
        List<Job> recommendedJobs = jobMatchCount.entrySet().stream()
                .sorted(Map.Entry.<Job, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return recommendedJobs;
    }
}
