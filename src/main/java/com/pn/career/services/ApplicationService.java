package com.pn.career.services;

import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.exceptions.DuplicateNameException;
import com.pn.career.exceptions.PermissionDenyException;
import com.pn.career.models.*;
import com.pn.career.repositories.*;
import com.pn.career.responses.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationService implements IApplicationService{
    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final ResumeRepository resumeRepository;
    private final EmailService emailService;
    private final StudentSkillRepository studentSkillRepository;
    private final JobCategoryRepository jobCategoryRepository;
    private final UserGrowthRepository userGrowthRepository;

    @Override
    public Application createApplication(Integer jobId, Integer resumeId) throws Exception {
        Job job=jobRepository.findById(jobId).orElseThrow(()->new RuntimeException("Không tìm thấy công việc"));
        Resume resume=resumeRepository.findById(resumeId).orElseThrow(()->new RuntimeException("Không tìm thấy hồ sơ"));
        if(applicationRepository.existsByJobIdAndStudentId(jobId,resume.getStudent().getUserId())){
            throw new DuplicateNameException("Hồ sơ đã được ứng tuyển");
        }
        Application application=Application.builder()
                .job(job)
                .resume(resume)
                .applicationStatus(ApplicationStatus.PENDING)
                .build();
        applicationRepository.save(application);
        log.info("Gửi email thông báo ứng tuyển cho sinh viên: "+resume.getStudent().getEmail());
        emailService.sendJobApplicationEmail(resume.getStudent().getEmail(), ResumeResponse.fromResume(resume), JobResponse.fromJob(job),ApplicationResponse.fromApplication(application));
        return application;
    }

    @Override
    public Page<ApplicationResponse> getAllApplicationByJobId(Integer employerId, Integer jobId, ApplicationStatus status, PageRequest pageRequest) {
        if(jobId==null){
            throw new RuntimeException("JobId không được để trống");
        }
        Job job=jobRepository.findById(jobId).orElseThrow(()->new RuntimeException("Không tìm thấy công việc"));
        if(job.getEmployer().getUserId()!=employerId){
            throw new PermissionDenyException("Bạn không có quyền xem hồ sơ ứng viên");
        }
        if(job==null){
            throw new DataNotFoundException("Không tìm thấy công việc");
        }
        log.info("Lấy danh sách hồ sơ ứng viên cho công việc: "+job.getJobTitle());
        Page<Application> applications=applicationRepository.findAllByJob_JobIdAndApplicationStatus(jobId,status,pageRequest);
        return applications.map(ApplicationResponse::fromApplication);
    }

    @Override
    public StudentApplicationResponse getApplicationById(Integer applicationId) {
        Application application=applicationRepository.findById(applicationId).orElseThrow(()->new DataNotFoundException("Không tìm thấy ứng viên"));
        Resume resume=application.getResume();
        List<StudentSkill> studentSkills=studentSkillRepository.findAllByStudent_UserId(resume.getStudent().getUserId());
        String categoryName;
        if (resume.getStudent().getJobCategory() == null) {
            categoryName = "Chưa cập nhật ngành nghề";
        } else {
            categoryName = jobCategoryRepository.findById(resume.getStudent().getJobCategory().getJobCategoryId())
                    .map(JobCategory::getJobCategoryName)
                    .orElse("Chưa cập nhật ngành nghề");
        }
        return StudentApplicationResponse.builder()
                .studentId(resume.getStudent().getUserId())
                .resumeId(resume.getResumeId())
                .lastName(resume.getStudent().getLastName())
                .firstName(resume.getStudent().getFirstName())
                .email(resume.getStudent().getEmail())
                .phoneNumber(resume.getStudent().getPhoneNumber())
                .profileImage(resume.getStudent().getProfileImage())
                .universityEmail(resume.getStudent().getUniversityEmail())
                .dob(resume.getStudent().getDob())
                .year(resume.getStudent().getYear())
                .provinceId(resume.getStudent().getProvinceId())
                .districtId(resume.getStudent().getDistrictId())
                .wardId(resume.getStudent().getWardId())
                .address(resume.getStudent().getAddress())
                .resumeFile(resume.getResumeFile())
                .studentSkills(studentSkills.stream().map(StudentSkillResponse::fromStudentSkill).toList())
                .categoryName(categoryName)
                .levelName(resume.getJobLevel().getNameLevel())
                .build();
    }

    @Override
    public Application updateStatus(Integer employerId, Integer applicationId, ApplicationStatus status) {
        Application application=applicationRepository.findById(applicationId).orElseThrow(()->new DataNotFoundException("Không tìm thấy hồ sơ ứng viên"));
        if(application.getJob().getEmployer().getUserId()!=employerId){
            throw new PermissionDenyException("Bạn không có quyền cập nhật trạng thái hồ sơ ứng viên");
        }
        application.setApplicationStatus(status);
       try{
           if(status ==ApplicationStatus.REJECTED){
               emailService.sendMailReject(application.getResume().getStudent().getEmail(),application.getResume().getStudent().getFirstName(), JobResponse.fromJob(application.getJob()));
           }
       }catch (Exception e) {
           throw new RuntimeException("Lỗi xảy ra khi gửi mail tới sinh viên");
       }
        return applicationRepository.save(application);
    }

    @Override
    public List<Map<String, Object>> getApplicationStatsByDate(LocalDateTime startDate, LocalDateTime endDate) {
        return userGrowthRepository.getApplicationStatsByDate(startDate, endDate);
    }
}
