package com.pn.career.services;
import com.pn.career.dtos.JobDTO;
import com.pn.career.models.*;
import com.pn.career.repositories.*;
import com.pn.career.responses.JobResponse;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class JobService implements IJobService {
    private final JobRepository jobRepository;
    private final EmployerRepository employerRepository;
    private final JobCategoryRepository jobCategoryRepository;
    private final JobLevelRepository jobLevelRepository;
    private final JobSkillService jobSkillService;
    private final JobSkillRepository jobSkillRepository;

    @Override
    @Transactional
    public JobResponse createJob(Integer employerId, JobDTO jobDTO) throws Exception {
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
                .isActive(true)
                .build();
        jobRepository.save(job);
        jobSkillService.createJobSkill(job, jobDTO.getSkillIds());
        JobResponse jobResponse=JobResponse.fromJob(job);
        List<JobSkill> jobSkills=jobSkillRepository.findAllByJob(job);
        jobResponse.setJobSkills(JobResponse.convertJobSkillToDTO(jobSkills));
        return jobResponse;
    }
    @Override
    public Optional<JobResponse> getJobById(Integer jobId) {
        Job job=jobRepository.findById(jobId).orElseThrow(()->new RuntimeException("Không tìm thấy công việc"));
        List<JobSkill> jobSkills=jobSkillRepository.findAllByJob(job);
        JobResponse jobResponse=JobResponse.fromJob(job);
        jobResponse.setJobSkills(JobResponse.convertJobSkillToDTO(jobSkills));
        return Optional.of(jobResponse);
    }
    @Override
    public Page<JobResponse> getJobsByEmployerId(Integer employerId, PageRequest page) {
        Employer employer=employerRepository.findById(employerId).orElseThrow(()->new RuntimeException("Không tìm thấy thông tin công ty"));
        Page<Job> jobs=jobRepository.findAllByEmployer(employer, page);
        return jobs.map(job -> {
            JobResponse jobResponse = JobResponse.fromJob(job);
            List<JobSkill> jobSkills = jobSkillRepository.findAllByJob(job);
            jobResponse.setJobSkills(JobResponse.convertJobSkillToDTO(jobSkills));
            return jobResponse;
        });
    }
    @Override
    @Transactional
    public JobResponse updateJob(Integer employerId, Integer jobId, JobDTO jobDTO) throws Exception {
        Job job=jobRepository.findById(jobId).orElseThrow(()->new Exception("Không tìm thấy thông tin công việc"));
        if (!Integer.valueOf(job.getEmployer().getUserId()).equals(employerId)) {
            throw new Exception("Bạn không có quyền chỉnh sửa công việc này");
        }
        JobCategory jobCategory=jobCategoryRepository.findById(jobDTO.getJobCategoryId()).orElseThrow(()->new Exception("Không tìm thấy thông tin danh mục công việc"));
        JobLevel jobLevel=jobLevelRepository.findById(jobDTO.getJobLevelId()).orElseThrow(()->new Exception("Không tìm thấy thông tin cấp độ công việc"));
        job.setJobCategory(jobCategory);
        job.setJobLevel(jobLevel);
        job.setJobTitle(jobDTO.getJobTitle());
        job.setJobDescription(jobDTO.getJobDescription());
        job.setJobRequirements(jobDTO.getJobRequirements());
        job.setJobLocation(jobDTO.getJobLocation());
        job.setJobMinSalary(jobDTO.getJobMinSalary());
        job.setJobMaxSalary(jobDTO.getJobMaxSalary());
        job.setAmount(jobDTO.getAmount());
        job.setJobDeadline(jobDTO.getJobDeadline());
        jobRepository.save(job);
        jobSkillService.updateJobSkill(job, jobDTO.getSkillIds());
        JobResponse jobResponse=JobResponse.fromJob(job);
        List<JobSkill> jobSkills=jobSkillRepository.findAllByJob(job);
        jobResponse.setJobSkills(JobResponse.convertJobSkillToDTO(jobSkills));
        return jobResponse;
    }

    @Override
    public Page<JobResponse> getAllJobs(PageRequest pageRequest) {
        Page<Job> jobs=jobRepository.findAll(pageRequest);
        return jobs.map(job -> {
            JobResponse jobResponse = JobResponse.fromJob(job);
            List<JobSkill> jobSkills = jobSkillRepository.findAllByJob(job);
            jobResponse.setJobSkills(JobResponse.convertJobSkillToDTO(jobSkills));
            return jobResponse;
        });
    }
}
