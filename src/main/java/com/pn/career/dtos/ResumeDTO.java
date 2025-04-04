package com.pn.career.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResumeDTO {
    private String resumeTitle;
    private String resumeFile;
    private String resumeDescription;
    private Integer levelId;
}
