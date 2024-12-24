package com.pn.career.services;

import com.pn.career.dtos.JobLevelDTO;
import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.models.JobLevel;
import com.pn.career.repositories.JobLevelRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
public class JobLevelService implements IJobLevelService {
    private final JobLevelRepository jobLevelRepository;
    @Override
    public List<JobLevel> findAllJobLevels(boolean isAdmin) {
        if(isAdmin){
            return jobLevelRepository.findAll();
        }
        return jobLevelRepository.findAllByIsActiveTrue();
    }

    @Override
    public JobLevel getJobLevelById(Integer id) {
        return jobLevelRepository.findById(id).orElseThrow(()-> new DataNotFoundException("Không tìm thấy cấp bậc tương ứng"));
    }

    @Override
    public JobLevel createJobLevel(JobLevelDTO jobLevelDTO) {
        if(jobLevelRepository.existsByNameLevel(jobLevelDTO.nameLevel())){
            throw new DataNotFoundException("Cấp bậc đã tồn tại");
        }
        JobLevel jobLevel=JobLevel.builder().nameLevel(jobLevelDTO.nameLevel()).build();
        jobLevel.setActive(true);
        return jobLevelRepository.save(jobLevel);
    }

    @Override
    @Transactional
    public JobLevel updateJobLevel(Integer jobLevelId, JobLevelDTO jobLevelDTO) {
        JobLevel jobLevel=getJobLevelById(jobLevelId);
        if(!jobLevel.getNameLevel().equals(jobLevelDTO.nameLevel())){
            if(jobLevelRepository.existsByNameLevel(jobLevelDTO.nameLevel())){
                throw new DataNotFoundException("Cấp bậc đã tồn tại");
            }
            jobLevel.setNameLevel(jobLevelDTO.nameLevel());
        }
        jobLevel.setActive(jobLevelDTO.active());
        return jobLevelRepository.save(jobLevel);
    }

    @Override
    @Transactional
    public void deleteJobLevel(Integer id) {
        JobLevel jobLevel=jobLevelRepository.findById(id).orElseThrow(()->new DataNotFoundException("Không tìm thấy cấp bậc tương ứng"));
        jobLevel.setActive(false);
        jobLevelRepository.save(jobLevel);
    }
}
