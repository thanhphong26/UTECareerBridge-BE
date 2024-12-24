package com.pn.career.services;

import com.pn.career.dtos.StudentDTO;
import com.pn.career.responses.ApplicationResponse;
import com.pn.career.responses.StudentResponse;
import com.pn.career.responses.StudentViewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
public interface IStudentService {
    StudentResponse updateStudent(Integer studentId, StudentDTO studentDTO);
    StudentResponse getStudentById(Integer studentId);
    void updateIsFindingJob(Integer studentId, boolean isFindingJob);
    Page<ApplicationResponse> getJobApplyByStudentId(Integer studentId, PageRequest pageRequest);
    Page<StudentViewResponse> getStudentIsFindingJob(Integer categoryId, PageRequest pageRequest);
}
