package com.pn.career.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ResumeJobMatchResponse {
    @JsonProperty("resume_id")
    private Integer resumeId;
    @JsonProperty("student_name")
    private String studentName;
    @JsonProperty("match_score")
    private Float matchScore;
    @JsonProperty("skill_match_score")
    private Float skillMatchScore;
    @JsonProperty("content_similarity")
    private Float contentSimilarity;
    @JsonProperty("matched_skills")
    private List<String> matchedSkills;
    @JsonProperty("missing_skills")
    private List<String> missingSkills;
    @JsonProperty("resume_title")
    private String resumeTitle;
    private String reason;

}
