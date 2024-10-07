package com.pn.career.services;

import com.pn.career.dtos.ResumeDTO;
import com.pn.career.models.Resume;

import java.util.List;

public interface IResumeService {
    Resume createResume(Integer studentId, ResumeDTO resumeDTO);
    List<Resume> getResumesByStudentId(Integer studentId);
    Resume getResumeById(Integer resumeId);
    Resume updateResume(Integer resumeId, ResumeDTO resumeDTO);
}
