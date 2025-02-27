package com.pn.career.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO extends SocialAccountDTO{
    @JsonProperty("phone_number")
    private String phoneNumber;
    @JsonProperty("email")
    private String email;
    @JsonProperty("password")
    private String password;
    @JsonProperty("google_account_id")
    private String googleAccountId;
    @JsonProperty("fullname")
    private String fullname;
    @JsonProperty("profile_image")
    private String profileImage;
    public boolean isPasswordBlank() {
        return password == null || password.trim().isEmpty();
    }
}
