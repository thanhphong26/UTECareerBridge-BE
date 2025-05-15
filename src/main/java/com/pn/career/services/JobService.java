package com.pn.career.services;
import com.pn.career.dtos.JobDTO;
import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.exceptions.PermissionDenyException;
import com.pn.career.models.*;
import com.pn.career.repositories.*;
import com.pn.career.responses.EmployerActivityStatsResponse;
import com.pn.career.responses.JobResponse;
import com.pn.career.responses.RecruitmentPerformanceResponse;
import com.pn.career.responses.TopSkillResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService implements IJobService {
    private final JobRepository jobRepository;
    private final EmployerRepository employerRepository;
    private final JobCategoryRepository jobCategoryRepository;
    private final JobLevelRepository jobLevelRepository;
    private final JobSkillService jobSkillService;
    private final JobSkillRepository jobSkillRepository;
    private final IEmployerPackageService employerPackageService;
    private final Logger logger= LoggerFactory.getLogger(JobService.class);
    private final InterviewRepository interviewRepository;
    private final JobRepositoryCustom jobRepositoryCustom;
    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    @Transactional
    public JobResponse createJob(Integer employerId, JobDTO jobDTO) throws Exception {
        Employer employer=employerRepository.findById(employerId).orElseThrow(()->new DataNotFoundException("Không tìm thấy thông tin công ty"));
        if(employer.getBusinessCertificate()== null || employer.getBusinessCertificate().isEmpty()){
            throw new Exception("Bạn cần cập nhật giấy phép kinh doanh trước khi thực hiện đăng tin tuyển dụng");
        }
        if(employer.getApprovalStatus()!= EmployerStatus.APPROVED){
            throw new Exception("Giấy phép kinh doanh của bạn chưa được duyệt, vui lòng chờ hoặc liên hệ với quản trị viên để được hỗ trợ");
        }
        JobCategory jobCategory=jobCategoryRepository.findById(jobDTO.getJobCategoryId()).orElseThrow(()->new DataNotFoundException("Không tìm thấy thông tin danh mục công việc"));
        JobLevel jobLevel=jobLevelRepository.findById(jobDTO.getJobLevelId()).orElseThrow(()->new DataNotFoundException("Không tìm thấy thông tin cấp độ công việc"));
        if(jobDTO.getPackageId()!=null){
            EmployerPackage employerPackage = employerPackageService.validateExpiredPackage(employerId, jobDTO.getPackageId());
            employerPackageService.updateEmployerPackage(employerId, jobDTO.getPackageId());
        }
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
                .status(JobStatus.PENDING)
                .packageId(jobDTO.getPackageId())
                .build();
        jobRepository.save(job);
        jobSkillService.createJobSkill(job, jobDTO.getSkillIds());
        JobResponse jobResponse=JobResponse.fromJob(job);
        List<JobSkill> jobSkills=jobSkillRepository.findAllByJob(job);
        jobResponse.setJobSkills(JobResponse.convertJobSkillToDTO(jobSkills));
        String jobUrl=frontendUrl+"/employer/view/"+job.getJobId();
       // fcmService.sendNotificationToAdmin("Có công việc mới cần duyệt", "Công ty "+employer.getCompanyName()+" vừa đăng một công việc mới, vui lòng kiểm tra và duyệt công việc", jobUrl);
        return jobResponse;
    }
    @Override
    public Optional<JobResponse> getJobById(Integer jobId, JobStatus jobStatus) {
        Job job=jobRepository.findJobByJobIdAndStatus(jobId, jobStatus);
        if(job==null){
            throw new DataNotFoundException("Không tìm thấy thông tin công việc");
        }
        List<JobSkill> jobSkills=jobSkillRepository.findAllByJob(job);
        JobResponse jobResponse=JobResponse.fromJob(job);
        jobResponse.setJobSkills(JobResponse.convertJobSkillToDTO(jobSkills));
        return Optional.of(jobResponse);
    }
    @Override
    public Page<JobResponse> getJobsByEmployerId(Integer employerId, PageRequest page) {
        Employer employer=employerRepository.findById(employerId).orElseThrow(()->new DataNotFoundException("Không tìm thấy thông tin công ty"));
        List<JobStatus> jobStatuses=List.of(JobStatus.ACTIVE);
        Page<Job> jobs=jobRepository.findAllByEmployerAndStatusIn(employer, jobStatuses , page);
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
        JobCategory jobCategory=jobCategoryRepository.findById(jobDTO.getJobCategoryId()).orElseThrow(()->new DataNotFoundException("Không tìm thấy thông tin danh mục công việc"));
        JobLevel jobLevel=jobLevelRepository.findById(jobDTO.getJobLevelId()).orElseThrow(()->new DataNotFoundException("Không tìm thấy thông tin cấp độ công việc"));
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
    public Page<JobResponse> getAllJobs(JobStatus status, PageRequest pageRequest) {
        Page<Job> jobs=jobRepository.findAllByStatusOrderByCreatedAtDesc(status, pageRequest);
        return jobs.map(job -> {
            JobResponse jobResponse = JobResponse.fromJob(job);
            List<JobSkill> jobSkills = jobSkillRepository.findAllByJob(job);
            jobResponse.setJobSkills(JobResponse.convertJobSkillToDTO(jobSkills));
            return jobResponse;
        });
    }
    @Override
    @Transactional
    public JobResponse approveJob(Integer jobId) {
        Job job=jobRepository.findById(jobId).orElseThrow(()->new DataNotFoundException("Không tìm thấy thông tin công việc"));
        job.setStatus(JobStatus.ACTIVE);
        jobRepository.save(job);
        JobResponse jobResponse=JobResponse.fromJob(job);
        List<JobSkill> jobSkills=jobSkillRepository.findAllByJob(job);
        jobResponse.setJobSkills(JobResponse.convertJobSkillToDTO(jobSkills));
        return jobResponse;
    }
    @Override
    @Transactional
    public JobResponse rejectJob(Integer jobId, String reasonReject) {
        Job job=jobRepository.findById(jobId).orElseThrow(()->new DataNotFoundException("Không tìm thấy thông tin công việc"));
        job.setStatus(JobStatus.REJECTED);
        job.setRejectionReason(reasonReject);
        jobRepository.save(job);
        JobResponse jobResponse=JobResponse.fromJob(job);
        List<JobSkill> jobSkills=jobSkillRepository.findAllByJob(job);
        jobResponse.setJobSkills(JobResponse.convertJobSkillToDTO(jobSkills));
        return jobResponse;
    }

    @Override
    @Transactional
    public JobResponse hideOrEnableJob(Integer employerId, Integer jobId, JobStatus jobStatus) {
        Job job=jobRepository.findById(jobId).orElseThrow(()->new DataNotFoundException("Không tìm thấy thông tin công việc"));
        if (!Integer.valueOf(job.getEmployer().getUserId()).equals(employerId)) {
            throw new RuntimeException("Bạn không có quyền ẩn công việc này");
        }
        job.setStatus(jobStatus);
        jobRepository.save(job);
        JobResponse jobResponse=JobResponse.fromJob(job);
        List<JobSkill> jobSkills=jobSkillRepository.findAllByJob(job);
        jobResponse.setJobSkills(JobResponse.convertJobSkillToDTO(jobSkills));
        return jobResponse;
    }



    @Override
    public Page<JobResponse> searchJob(String keyword, Integer jobCategoryId, Integer industryId, Integer jobLevelId, Integer skillId, String sorting, PageRequest pageRequest) {
        Page<Job> jobs=jobRepository.search(keyword, jobCategoryId, industryId, jobLevelId, skillId, sorting, pageRequest);
        logger.info("Total elements: " + jobs.getTotalElements());
        logger.info("Total pages: " + jobs.getTotalPages());
        return jobs.map(job -> {
            JobResponse jobResponse = JobResponse.fromJob(job);
            List<JobSkill> jobSkills = jobSkillRepository.findAllByJob(job);
            jobResponse.setJobSkills(JobResponse.convertJobSkillToDTO(jobSkills));
            return jobResponse;
        });
    }

    @Override
    public Page<JobResponse> getJobByStatus(Integer employerId, JobStatus jobStatus, PageRequest pageRequest) {
        Page<Job> jobs= jobRepository.findAllByEmployer_UserIdAndStatus(employerId, jobStatus, pageRequest);
        return jobs.map(job -> {
            JobResponse jobResponse = JobResponse.fromJob(job);
            List<JobSkill> jobSkills = jobSkillRepository.findAllByJob(job);
            jobResponse.setJobSkills(JobResponse.convertJobSkillToDTO(jobSkills));
            return jobResponse;
        });
    }

    @Override
    public void deleteJob(Integer employerId, Integer jobId) {
        Job job=jobRepository.findById(jobId).orElseThrow(()->new DataNotFoundException("Không tìm thấy thông tin công việc"));
        if (!Integer.valueOf(job.getEmployer().getUserId()).equals(employerId)) {
            throw new PermissionDenyException("Bạn không có quyền xóa công việc này");
        }
        jobRepository.delete(job);
    }

    @Override
    public List<JobResponse> getSimilarJobs(Integer jobId) {
        Job job=jobRepository.findById(jobId).orElseThrow(()->new DataNotFoundException("Không tìm thấy thông tin công việc"));
        if(job.getStatus()!=JobStatus.ACTIVE){
            throw new DataNotFoundException("Công việc đã bị ẩn bởi nhà tuyển dụng hoặc vi phạm quy định của hệ thống");
        }
        List<Job> jobs=jobRepository.getSimilarJobs(jobId);
        return jobs.stream().map(j -> {
            JobResponse jobResponse = JobResponse.fromJob(j);
            List<JobSkill> jobSkills = jobSkillRepository.findAllByJob(j);
            jobResponse.setJobSkills(JobResponse.convertJobSkillToDTO(jobSkills));
            return jobResponse;
        }).collect(Collectors.toList());
    }
    @Override
    public Integer countJobByEmployerId(Integer employerId) {
        return jobRepository.countByEmployer_UserId(employerId);
    }

    @Override
    public Page<JobResponse> getJobRecruitmentUrgent(PageRequest pageRequest) {
        Integer packageId=6;
        Page<Job> jobs=jobRepository.findActiveJobsByPackageIdOrderByCreatedAtDesc(packageId, pageRequest);
        return jobs.map(job -> {
            JobResponse jobResponse = JobResponse.fromJob(job);
            List<JobSkill> jobSkills = jobSkillRepository.findAllByJob(job);
            jobResponse.setJobSkills(JobResponse.convertJobSkillToDTO(jobSkills));
            return jobResponse;
        });
    }

    @Override
    public Integer countJobByActiveStatus(JobStatus jobStatus) {
        return jobRepository.countByStatus(jobStatus);
    }
    @Override
    public Integer countJobByEmployerIdAndStatus(Integer employerId, JobStatus jobStatus) {
        return jobRepository.countByEmployer_UserIdAndStatus(employerId, jobStatus);
    }
    @Override
    public Integer countJobByJobCategoryIdAndStatus(Integer jobCategoryId, JobStatus jobStatus) {
        return jobRepository.countByJobCategory_JobCategoryIdAndStatus(jobCategoryId, jobStatus);
    }

    @Override
    public Double timeAverageRecruitment(Integer employerId) {
        return jobRepository.getAverageRecruitmentTimeForEmployer(employerId);
    }


    public List<EmployerActivityStatsResponse> getEmployerActivityStats(Integer employerId, Integer month, Integer year) {
        List<Object[]> rawStats = jobRepository.getEmployerActivityStats(employerId, month, year);
        return rawStats.stream()
                .map(record -> new EmployerActivityStatsResponse(
                        (Date) record[0],
                        ((Number) record[1]).longValue(),
                        ((Number) record[2]).longValue()))
                .toList();
    }
    public Page<JobResponse> getJobCompleteInterviewRecentByEmployerId(Integer employerId, PageRequest pageRequest) {
        Page<Integer> jobIds = interviewRepository.findRecentCompletedInterviewJobIdsByEmployerId(employerId, pageRequest);
        return jobIds.map(jobId -> {
            Job job = jobRepository.findById(jobId).orElseThrow(); // Assuming you have a jobRepository
            JobResponse jobResponse = JobResponse.fromJob(job);
            List<JobSkill> jobSkills = jobSkillRepository.findAllByJob(job);
            jobResponse.setJobSkills(JobResponse.convertJobSkillToDTO(jobSkills));
            return jobResponse;
        });
    }

    @Override
    public List<TopSkillResponse> getTopSkillsInJobByEmployerId(Integer employerId, Integer limit, LocalDateTime startDate, LocalDateTime endDate) {
        return jobRepositoryCustom.getTopSkillsByEmployerId(employerId, limit, startDate, endDate);
    }

    @Override
    public List<RecruitmentPerformanceResponse> getJobsRecruitmentPerformance(Integer employerId, LocalDateTime startDate, LocalDateTime endDate, Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Employer employer = employerRepository.findById(employerId).orElseThrow(() -> new DataNotFoundException("Không tìm thấy thông tin công ty"));
        Page<Job> jobs = jobRepository.findAllByEmployerAndStatusIn(employer, List.of(JobStatus.ACTIVE, JobStatus.INACTIVE), pageRequest);
        List<Job> jobList = jobs.getContent();
        return jobList.stream().map(job -> {
            // Get view count for this specific job within date range
            Integer viewCount = jobRepository.countJobViewsInDateRange(
                    job.getJobId(), ActionType.VIEW, startDate, endDate);

            // Get application count for this job within date range
            Integer applicationCount = jobRepository.countJobApplicationsInDateRange(
                    job.getJobId(), startDate, endDate);

            return RecruitmentPerformanceResponse.builder()
                    .jobId(job.getJobId())
                    .title(job.getJobTitle())
                    .views(viewCount)
                    .applications(applicationCount)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public List<TopSkillResponse> getTopApplicantSkillsByEmployerId(Integer employerId, Integer limit, LocalDateTime startDate, LocalDateTime endDate) {
        return jobRepositoryCustom.getTopApplicantSkillsByEmployerId(employerId, limit, startDate, endDate);
    }
}
