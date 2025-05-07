package com.pn.career.services;

import com.pn.career.dtos.CategoryJobDTO;
import com.pn.career.dtos.CategoryJobUpdateDTO;
import com.pn.career.models.Job;
import com.pn.career.models.JobCategory;
import com.pn.career.repositories.JobCategoryRepository;
import com.pn.career.repositories.JobRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class JobCategoryService implements IJobCategoryService{
    private final JobCategoryRepository jobCategoryRepository;
    private final JobRepository jobRepository;
    @Override
    public List<JobCategory> findAllJobCategories(boolean isAdmin) {
        if(isAdmin){
            return jobCategoryRepository.findAll();
        }
        return jobCategoryRepository.findAllByIsActiveTrue();
    }

    @Override
    public JobCategory getJobCategoryById(Integer jobCategoryId) {
        return jobCategoryRepository.findById(jobCategoryId).orElseThrow(()->new RuntimeException("Không tìm thấy ngành nghề"));
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
    @Override
    @Transactional
    public JobCategory updateJobCategory(Integer jobCategoryId, CategoryJobUpdateDTO jobCategory) {
        JobCategory existingJobCategory=getJobCategoryById(jobCategoryId);
        if(!existingJobCategory.getJobCategoryName().equals(jobCategory.getCategoryJobName())){
            if(jobCategoryRepository.existsByJobCategoryName(jobCategory.getCategoryJobName())){
                throw new DuplicateKeyException("Ngành nghề "+jobCategory.getCategoryJobName()+" đã tồn tại");
            }
            existingJobCategory.setJobCategoryName(jobCategory.getCategoryJobName());
        }
        existingJobCategory.setActive(jobCategory.isActive());
        return jobCategoryRepository.save(existingJobCategory);
    }

    @Override
    @Transactional
    public void deleteJobCategory(Integer jobCategoryId) throws Exception {
        JobCategory existingJobCategory = getJobCategoryById(jobCategoryId);
        List<Job> jobs = jobRepository.findAllByJobCategory(existingJobCategory);
        if(!jobs.isEmpty()){
            throw new Exception("Không thể xóa ngành nghề này vì có công việc đang sử dụng");
        }else{
            jobCategoryRepository.delete(existingJobCategory);
        }
    }

    @Override
    public List<JobCategory> getJobCategoryByName(String name) {
        List<JobCategory> jobCategories = jobCategoryRepository.findByJobCategoryNameContainingIgnoreCase(name);
        if(jobCategories.isEmpty()){
            throw new RuntimeException("Không tìm thấy ngành nghề nào");
        }
        return jobCategories;
    }

    @Override
    public int countJobCategory(boolean isActive) {
        if(isActive){
            return jobCategoryRepository.findAllByIsActiveTrue().size();
        }else{
            return jobCategoryRepository.findAll().size();
        }
    }
}
