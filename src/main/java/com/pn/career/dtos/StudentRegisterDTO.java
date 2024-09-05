package com.pn.career.dtos;

import com.pn.career.models.Role;
import com.pn.career.models.Student;
import com.pn.career.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
@Data
@Builder
@AllArgsConstructor
public class StudentRegisterDTO extends RegistrationDTO{

    @Override
    public User createUser(Role role) {
        return Student.builder()
                .firstName(getFirsName())
                .lastName(getLastName())
                .phoneNumber(getPhoneNumber())
                .email(getEmail())
                .universityEmail(getEmail())
                .role(role)
                .active(true)
                .build();
    }
}
