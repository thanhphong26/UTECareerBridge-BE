package com.pn.career.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pn.career.models.Role;
import com.pn.career.models.User;
import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentResponse {
    @JsonProperty("id")
    private int id;
    @JsonProperty("phone_number")
    private String phoneNumber;
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("gender")
    private boolean gender;
    @JsonProperty("dob")
    private LocalDate dob;
    @JsonProperty("province_id")
    private int provinceId;
    @JsonProperty("district_id")
    private int districtId;
    @JsonProperty("ward_id")
    private int wardId;
    @JsonProperty("address")
    private String address;
    @JsonProperty("role")
    private Role role;
    public static StudentResponse fromUser(User user) {
        return StudentResponse.builder()
                .id(user.getUserId())
                .phoneNumber(user.getPhoneNumber())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .gender(user.isGender())
                .dob(user.getDob())
                .provinceId(user.getProvinceId())
                .districtId(user.getDistrictId())
                .wardId(user.getWardId())
                .address(user.getAddress())
                .role(user.getRole())
                .build();
    }
}
