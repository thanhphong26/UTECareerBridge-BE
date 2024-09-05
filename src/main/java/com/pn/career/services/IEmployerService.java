package com.pn.career.services;

import com.pn.career.dtos.EmployerUpdateDTO;
import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.models.Employer;

public interface IEmployerService{
    Employer updateEmployer(Integer employerId, EmployerUpdateDTO employerUpdateDTO);
    Employer getEmployerById(Integer employerId) throws DataNotFoundException;
}
