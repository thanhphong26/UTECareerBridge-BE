package com.pn.career.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pn.career.models.Employer;
import com.pn.career.models.Role;
import com.pn.career.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class EmployerRegisterDTO extends RegistrationDTO{
    @JsonProperty("company_name")
    private String companyName;
    @JsonProperty("company_email")
    private String companyEmail;
    @JsonProperty("company_address")
    private String companyAddress;
    @JsonProperty("company_website")
    private String companyWebsite;

    @Override
    public User createUser(Role role) {
        Employer employer=new Employer();
        employer.setFirstName(getFirsName());
        employer.setLastName(getLastName());
        employer.setPhoneNumber(getPhoneNumber());
        employer.setGender(isGender());
        employer.setDob(getDob());
        employer.setEmail(getEmail());
        employer.setRole(role);
        employer.setCompanyName(companyName);
        employer.setCompanyEmail(companyEmail);
        employer.setCompanyAddress(companyAddress);
        employer.setCompanyWebsite(companyWebsite);
        employer.setActive(true);
        return employer;
    }
}
