package com.pn.career.responses;

import lombok.*;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopSkillResponse {
    private Integer skillId;
    private String skillName;
    private Long value;
}
