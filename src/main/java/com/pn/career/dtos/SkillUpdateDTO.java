package com.pn.career.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkillUpdateDTO {
    @NotBlank(message = "Tên kỹ năng không được để trống")
    private String skillName;
    private boolean active;
}
