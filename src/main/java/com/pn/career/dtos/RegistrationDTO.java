package com.pn.career.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pn.career.models.Role;
import com.pn.career.models.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class RegistrationDTO {
    @JsonProperty("first_name")
    private String firsName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("phone_number")
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Invalid phone number format")
    private String phoneNumber;
    @JsonProperty("email")
    private String email;
    @JsonProperty("gender")
    private boolean gender;
    @JsonProperty("dob")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dob;
    @NotBlank(message = "Password cannot be blank")
    private String password;
    @JsonProperty("retype_password")
    private String retypePassword;
    public abstract User createUser(Role role);
}
