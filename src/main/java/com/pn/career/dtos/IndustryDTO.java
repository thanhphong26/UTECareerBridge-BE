package com.pn.career.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IndustryDTO {
    @NotBlank(message = "Tên loại hình công ty không được bỏ trống")
    private String industryName;
}
