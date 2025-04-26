package com.pn.career.dtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobAlertDTO {
    private String jobTitle;
    private Double minSalary;
    private List<Integer> level;
    private String location;
    private Integer jobCategoryId;
    private List<Integer> companyField;
    private String frequency;
    private boolean notifyByEmail;
    private boolean notifyByApp;
}
