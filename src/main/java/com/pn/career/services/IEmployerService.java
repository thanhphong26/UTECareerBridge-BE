package com.pn.career.services;

import com.pn.career.dtos.EmployerUpdateDTO;
import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.models.Employer;
import com.pn.career.responses.EmployerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

public interface IEmployerService{
    Employer updateEmployer(Integer employerId, EmployerUpdateDTO employerUpdateDTO);
    Employer getEmployerById(Integer employerId) throws DataNotFoundException;
    Page<EmployerResponse>getAllEmployers(String keyword, Integer industryId, PageRequest pageRequest);
    void addBusinessCertificate(Integer employerId, MultipartFile businessCertificate);
    void approveEmployer(Integer employerId);
    void rejectEmployer(Integer employerId, String reason);
}
