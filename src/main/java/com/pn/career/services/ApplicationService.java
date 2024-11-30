package com.pn.career.services;

import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.exceptions.DuplicateNameException;
import com.pn.career.models.*;
import com.pn.career.repositories.*;
import com.pn.career.responses.ApplicationResponse;
import com.pn.career.responses.StudentApplicationResponse;
import com.pn.career.responses.StudentSkillResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService implements IApplicationService{
    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final ResumeRepository resumeRepository;
    private final EmailService emailService;
    private final StudentSkillRepository studentSkillRepository;
    private final JobCategoryRepository jobCategoryRepository;

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
        emailService.sendJobApplicationEmail(resume.getStudent().getEmail(), ApplicationResponse.fromApplication(application));
        return application;
    }

    @Override
    public List<Application> getAllApplicationByJobId(Integer jobId) {
        if(jobId==null){
            throw new RuntimeException("JobId không được để trống");
        }
        Job job=jobRepository.findById(jobId).orElseThrow(()->new RuntimeException("Không tìm thấy công việc"));
        if(job==null){
            throw new DataNotFoundException("Không tìm thấy công việc");
        }
        return applicationRepository.findAllByJob_JobId(jobId);
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
                .lastName(resume.getStudent().getLastName())
                .firstName(resume.getStudent().getFirstName())
                .email(resume.getStudent().getEmail())
                .phoneNumber(resume.getStudent().getPhoneNumber())
                .address(resume.getStudent().getAddress())
                .resumeFile(resume.getResumeFile())
                .studentSkills(studentSkills.stream().map(StudentSkillResponse::fromStudentSkill).toList())
                .categoryName(categoryName)
                .build();
    }
}
