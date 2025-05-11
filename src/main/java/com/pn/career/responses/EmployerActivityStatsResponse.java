package com.pn.career.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class EmployerActivityStatsResponse {
    private Date date;
    private Long views;
    private Long applications;
}