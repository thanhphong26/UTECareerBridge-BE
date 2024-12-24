package com.pn.career.services;

import com.pn.career.models.Application;
import com.pn.career.models.ApplicationStatus;
import com.pn.career.responses.ApplicationResponse;
import com.pn.career.responses.StudentApplicationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IApplicationService {
    Application createApplication(Integer jobId, Integer resumeId) throws Exception;
    Page<ApplicationResponse> getAllApplicationByJobId(Integer employerId, Integer jobId, ApplicationStatus status, PageRequest pageRequest);
    StudentApplicationResponse getApplicationById(Integer applicationId);
    Application updateStatus(Integer employerId, Integer applicationId, ApplicationStatus status);
}
