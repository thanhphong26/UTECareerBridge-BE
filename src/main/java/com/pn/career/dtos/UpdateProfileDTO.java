package com.pn.career.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileDTO {
    private String firstName;
    private String lastName;
    private boolean gender;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dob;
    private String phoneNumber;
}
