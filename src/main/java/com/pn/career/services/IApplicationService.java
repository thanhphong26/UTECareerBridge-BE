package com.pn.career.services;

import com.pn.career.models.Application;

public interface IApplicationService {
    Application createApplication(Integer jobId, Integer resumeId) throws Exception;
}
