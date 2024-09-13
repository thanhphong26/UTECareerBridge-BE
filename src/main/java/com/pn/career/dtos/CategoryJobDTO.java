package com.pn.career.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data//toString
@AllArgsConstructor
@NoArgsConstructor
public class CategoryJobDTO {
    @JsonProperty("category_job_name")
    @NotEmpty(message = "Tên nghề nghiệp không được để trống")
    private String categoryJobName;
}
