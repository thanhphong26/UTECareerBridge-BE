package com.pn.career.services;

import com.pn.career.dtos.EmployerUpdateDTO;
import com.pn.career.dtos.UpdatePasswordDTO;
import com.pn.career.dtos.UpdateProfileDTO;
import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.models.Employer;
import com.pn.career.models.EmployerStatus;
import com.pn.career.responses.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface IEmployerService{
    Integer countEmployerByStatus(EmployerStatus status);
    Employer updateEmployer(Integer employerId, EmployerUpdateDTO employerUpdateDTO);
    Employer getEmployerById (Integer employerId) throws DataNotFoundException;
    Employer updateProfile(Integer employerId, UpdateProfileDTO updateProfileDTO);
    Employer updatePassword(Integer employerId, UpdatePasswordDTO updatePasswordDTO);
    void revokedTokens(Integer employerId);
    Page<EmployerResponse>getAllEmployers(String keyword, Integer industryId, PageRequest pageRequest, EmployerStatus status);
    Employer addBusinessCertificate(Integer employerId, String businessCertificate);
    void approveEmployer(Integer employerId);
    void rejectEmployer(Integer employerId, String reason);
    List<StudentResponse> getStudentsByApplication(Integer employerId);
    Page<EmployerResponse> getEmployersByIndustry(Integer industryId, PageRequest pageRequest);
    Page<TopEmployerResponse> getTopEmployersByJobCount(PageRequest pageRequest);
    Integer getTotalJobCount(Integer employerId);
    List<EmployerResponse> getAllEmployerByJobCategoryAndStatus(String categoryName, EmployerStatus status);
    Map<String, Object> getEmployerStatistics(Integer employerId);
}
