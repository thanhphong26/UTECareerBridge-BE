package com.pn.career.services;

import com.pn.career.dtos.ResumeDTO;
import com.pn.career.models.Resume;

import java.util.List;

public interface IResumeService {
    Resume createResume(Integer studentId, ResumeDTO resumeDTO);
    List<Resume> getResumesByStudentId(Integer studentId);
    Resume getResumeById(Integer studentId, Integer resumeId);
    Resume updateResume(Integer resumeId, ResumeDTO resumeDTO);
    void deleteResume(Integer studentId, Integer resumeId);
    boolean updateActiveResume(Integer resumeId, Integer studentId, boolean isActive);
}
