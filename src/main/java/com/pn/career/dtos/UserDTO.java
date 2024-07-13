package com.pn.career.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDTO {

    @Size(min = 1, max = 15)
    @JsonProperty("phone_number")
    private String phoneNumber;

    @NotNull
    @Email
    private String email;

    @NotNull
    @Size(min = 1, max = 50)
    @JsonProperty("first_name")
    private String firstName;

    @NotNull
    @Size(min = 1, max = 50)
    @JsonProperty("last_name")
    private String lastName;

    @NotNull
    @JsonProperty("role_id")
    private int roleId;

    @NotNull
    @Size(min = 8, max = 20)
    private String password;

}

