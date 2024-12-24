package com.pn.career.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentDTO {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private boolean gender;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dob;
    private Integer provinceId;
    private Integer districtId;
    private Integer wardId;
    private String address;
    private String universityEmail;
    private Integer year;
    private String profileImage;
    private Integer categoryId;
}
