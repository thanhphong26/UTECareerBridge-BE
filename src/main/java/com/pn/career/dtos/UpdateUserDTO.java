package com.pn.career.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDTO {
    private String phone;
    private String email;
    private String firstName;
    private String lastName;
    private Boolean active;
    private String address;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dob;
    private Integer userId;

}
