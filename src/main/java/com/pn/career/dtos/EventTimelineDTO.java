package com.pn.career.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EventTimelineDTO {
    private Integer timelineId;
    private String timelineTitle;
    private String timelineDescription;
    private LocalTime timelineStart;
}
