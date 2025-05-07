package com.pn.career.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CVJobMatchResponse {
    @JsonProperty("job_id")
    private int jobId;
    @JsonProperty("job_title")
    private String jobTitle;
    @JsonProperty("match_score")
    private float matchScore;
    @JsonProperty("matched_skills")
    private List<String> matchedSkills;
    @JsonProperty("missing_skills")
    private List<String> missingSkills;
    @JsonProperty("employer_id")
    private int employerId;
    private String logo;
    @JsonProperty("company_name")
    private String companyName;
    @JsonProperty("job_location")
    private String jobLocation;
    @JsonProperty("job_min_salary")
    private Float jobMinSalary;
    @JsonProperty("job_max_salary")
    private Float jobMaxSalary;
}