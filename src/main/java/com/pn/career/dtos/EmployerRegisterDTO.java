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
    @JsonProperty("company_address")
    private String companyAddress;

    @Override
    public User createUser(Role role) {
        Employer employer=new Employer();
        employer.setFirstName(getFirsName());
        employer.setLastName(getLastName());
        employer.setPhoneNumber(getPhoneNumber());
        employer.setEmail(getEmail());
        employer.setRole(role);
        employer.setCompanyName(companyName);
        employer.setCompanyAddress(companyAddress);
        employer.setActive(true);
        return employer;
    }
}
