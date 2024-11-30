package com.pn.career.responses;

import com.pn.career.models.StudentSkill;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentSkillResponse {
    private Integer studentId;
    private Integer skillId;
    private String skillName;
    private Integer level;
    public static StudentSkillResponse fromStudentSkill(StudentSkill studentSkill){
        return StudentSkillResponse.builder()
                .studentId(studentSkill.getStudent().getUserId())
                .skillId(studentSkill.getSkill().getSkillId())
                .skillName(studentSkill.getSkill().getSkillName())
                .level(studentSkill.getLevel())
                .build();
    }
}
