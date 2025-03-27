package com.pn.career.services;

import com.pn.career.models.StudentCV;

import java.util.List;

public interface IStudentCVService {
    StudentCV createStudentCV(StudentCV studentCV);
    List<StudentCV> findAllByStudentId(Integer studentId);
    StudentCV updateStudentCV(StudentCV studentCV);
    void deleteStudentCV(StudentCV studentCV);
}
