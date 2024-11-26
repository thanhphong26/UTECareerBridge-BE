package com.pn.career.services;

import com.pn.career.dtos.StudentDTO;
import com.pn.career.responses.StudentResponse;

public interface IStudentService {
    StudentResponse updateStudent(Integer studentId, StudentDTO studentDTO);
}
