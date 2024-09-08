package com.pn.career.services;
import com.pn.career.dtos.JobDTO;
import com.pn.career.models.Employer;
import com.pn.career.models.Job;
import com.pn.career.models.JobCategory;
import com.pn.career.models.JobLevel;
import com.pn.career.repositories.EmployerRepository;
import com.pn.career.repositories.JobCategoryRepository;
import com.pn.career.repositories.JobLevelRepository;
import com.pn.career.repositories.JobRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@AllArgsConstructor
public class JobService implements IJobService {
    private final JobRepository jobRepository;
    private final EmployerRepository employerRepository;
    private final JobCategoryRepository jobCategoryRepository;
    private final JobLevelRepository jobLevelRepository;
    @Override
    public List<Job> findAllJobs(boolean isAdmin) {
        return jobRepository.findAll();
    }

    @Override
    public Job createJob(Integer employerId, JobDTO jobDTO) throws Exception {
        Employer employer=employerRepository.findById(employerId).orElseThrow(()->new Exception("Không tìm thấy thông tin công ty"));
        JobCategory jobCategory=jobCategoryRepository.findById(jobDTO.getJobCategoryId()).orElseThrow(()->new Exception("Không tìm thấy thông tin danh mục công việc"));
        JobLevel jobLevel=jobLevelRepository.findById(jobDTO.getJobLevelId()).orElseThrow(()->new Exception("Không tìm thấy thông tin cấp độ công việc"));

        Job job=Job.builder()
                .jobCategory(jobCategory)
                .jobLevel(jobLevel)
                .jobTitle(jobDTO.getJobTitle())
                .jobDescription(jobDTO.getJobDescription())
                .jobRequirements(jobDTO.getJobRequirements())
                .jobLocation(jobDTO.getJobLocation())
                .jobMinSalary(jobDTO.getJobMinSalary())
                .jobMaxSalary(jobDTO.getJobMaxSalary())
                .amount(jobDTO.getAmount())
                .jobDeadline(jobDTO.getJobDeadline())
                .employer(employer)
                .build();
        return jobRepository.save(job);
    }
}
