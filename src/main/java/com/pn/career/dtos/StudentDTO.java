package com.pn.career.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StudentDTO {
    @JsonProperty("university_mail")
    @Size(max = 100)
    @NotNull
    private String universityMail;

    @JsonProperty("profile_image")
    @Size(max=255)
    private String profileImage;

    @Size(max = 255)
    private String address;

}
