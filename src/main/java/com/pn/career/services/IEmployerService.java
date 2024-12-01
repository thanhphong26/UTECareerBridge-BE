package com.pn.career.services;

import com.pn.career.dtos.EmployerUpdateDTO;
import com.pn.career.dtos.UpdatePasswordDTO;
import com.pn.career.dtos.UpdateProfileDTO;
import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.models.Employer;
import com.pn.career.models.EmployerStatus;
import com.pn.career.responses.EmployerResponse;
import com.pn.career.responses.JobResponse;
import com.pn.career.responses.StudentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IEmployerService{
    Employer updateEmployer(Integer employerId, EmployerUpdateDTO employerUpdateDTO);
    Employer getEmployerById(Integer employerId) throws DataNotFoundException;
    Employer updateProfile(Integer employerId, UpdateProfileDTO updateProfileDTO);
    Employer updatePassword(Integer employerId, UpdatePasswordDTO updatePasswordDTO);
    void revokedTokens(Integer employerId);
    Page<EmployerResponse>getAllEmployers(String keyword, Integer industryId, PageRequest pageRequest, EmployerStatus status);
    Employer addBusinessCertificate(Integer employerId, MultipartFile businessCertificate);
    void approveEmployer(Integer employerId);
    void rejectEmployer(Integer employerId, String reason);
    List<StudentResponse> getStudentsByApplication(Integer employerId);
}
