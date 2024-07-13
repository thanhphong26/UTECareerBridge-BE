package com.pn.career.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SkillDTO {

    @NotNull
    @Size(max = 100)
    @JsonProperty("skill_name")
    private String skillName;

    @Size(max = 50)
    @JsonProperty("skill_level")
    private String skillLevel;
}
