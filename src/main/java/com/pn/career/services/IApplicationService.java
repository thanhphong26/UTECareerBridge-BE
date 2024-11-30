package com.pn.career.services;

import com.pn.career.models.Application;
import com.pn.career.responses.StudentApplicationResponse;

import java.util.List;

public interface IApplicationService {
    Application createApplication(Integer jobId, Integer resumeId) throws Exception;
    List<Application> getAllApplicationByJobId(Integer jobId);
    StudentApplicationResponse getApplicationById(Integer applicationId);
}
