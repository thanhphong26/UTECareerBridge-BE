package com.pn.career.services;

import com.pn.career.dtos.StudentDTO;
import com.pn.career.responses.ApplicationResponse;
import com.pn.career.responses.StudentResponse;

import java.util.List;

public interface IStudentService {
    StudentResponse updateStudent(Integer studentId, StudentDTO studentDTO);
    StudentResponse getStudentById(Integer studentId);
    void updateIsFindingJob(Integer studentId, boolean isFindingJob);
    List<ApplicationResponse> getJobApplyByStudentId(Integer studentId);

}
