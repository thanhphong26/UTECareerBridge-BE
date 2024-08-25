package com.pn.career.services;

import com.pn.career.models.Employer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IEmployerService extends JpaRepository<Employer, Integer> {
    //Employer updateEmployer(Integer employerId, EmployerUpdateDTO employerUpdateDTO);
}
