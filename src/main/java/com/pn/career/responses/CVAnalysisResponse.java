package com.pn.career.responses;

import com.pn.career.dtos.CVEducationItem;
import com.pn.career.dtos.CVExperienceItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CVAnalysisResponse {
    private boolean success;
    private List<String> skills;
    private List<CVEducationItem> education;
    private List<CVExperienceItem> experience;
    private String error;
}