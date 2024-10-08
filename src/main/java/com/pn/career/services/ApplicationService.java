package com.pn.career.services;

import com.pn.career.exceptions.DuplicateNameException;
import com.pn.career.models.Application;
import com.pn.career.models.ApplicationStatus;
import com.pn.career.models.Job;
import com.pn.career.models.Resume;
import com.pn.career.repositories.ApplicationRepository;
import com.pn.career.repositories.JobRepository;
import com.pn.career.repositories.ResumeRepository;
import com.pn.career.responses.ApplicationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicationService implements IApplicationService{
    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final ResumeRepository resumeRepository;
    private final EmailService emailService;
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
}
