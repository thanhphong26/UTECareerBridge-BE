package com.pn.career.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class JobCategoryDTO {

    @NotNull
    @Size(max = 100)
    @JsonProperty("category_name")
    private String jobCategoryName;
}
