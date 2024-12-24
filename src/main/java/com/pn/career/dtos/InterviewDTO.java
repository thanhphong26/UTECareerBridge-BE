package com.pn.career.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InterviewDTO {
    private Integer studentId;
    private Integer jobId;
    private String jobPosition;
    private String interviewDate;
    private String interviewMethod;
    private String interviewLocation;
    private String interviewer;
    private String contactEmail;
    private String contactPhone;
    private String description;
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("jobPosition", jobPosition);
        map.put("interviewDate", interviewDate);
        map.put("interviewMethod", interviewMethod);
        map.put("interviewLocation", interviewLocation);
        map.put("interviewer", interviewer);
        map.put("contactEmail", contactEmail);
        map.put("contactPhone", contactPhone);
        map.put("description", description);
        return map;
    }

}
