package com.pn.career.services;

import com.pn.career.dtos.CategoryJobDTO;
import com.pn.career.models.JobCategory;
import com.pn.career.repositories.JobCategoryRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class JobCategoryService implements IJobCategoryService{
    private final JobCategoryRepository jobCategoryRepository;
    @Override
    public List<JobCategory> findAllJobCategories(boolean isAdmin) {
        if(isAdmin){
            return jobCategoryRepository.findAll();
        }
        return jobCategoryRepository.findAllByIsActiveTrue();
    }
    @Transactional
    @Override
    public JobCategory createJobCategory(CategoryJobDTO jobCategory) {
        if(jobCategoryRepository.existsByJobCategoryName(jobCategory.getCategoryJobName())){
            throw new DuplicateKeyException("Ngành nghề "+jobCategory.getCategoryJobName()+" đã tồn tại");
        }
       JobCategory newJobCategory = JobCategory.builder()
               .jobCategoryName(jobCategory.getCategoryJobName())
               .isActive(true)
               .build();
       return jobCategoryRepository.save(newJobCategory);
    }
}
