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
public class ResumeDTO {

    @NotNull
    @Size(max = 100)
    @JsonProperty("resume_title")
    private String resumeTitle;

    @Size(max = 255)
    @JsonProperty("resume_file")
    private String resumeFile;

    @JsonProperty("resume_description")
    private String resumeDescription;
}
