package com.pn.career.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentRegistrationDTO {
    @JsonProperty("first_name")
    private String firsName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("phone_number")
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    @JsonProperty("email")
    private String email;
    @NotBlank(message = "Password cannot be blank")
    private String password;
    @JsonProperty("retype_password")
    private String retypePassword;
}
