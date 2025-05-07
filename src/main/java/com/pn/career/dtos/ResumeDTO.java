package com.pn.career.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResumeDTO {
    private String resumeTitle;
    private String resumeFile;
    private String resumeDescription;
    private Integer levelId;
    private Object theme;
    private Object personalInfo;
    private Object sections;
    private Object workExperiences;
    private Object certificates;
    private Object skills;
}
