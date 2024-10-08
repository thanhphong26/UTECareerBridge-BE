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
    private Integer level;
    public static StudentSkillResponse fromStudentSkill(StudentSkill studentSkill){
        return StudentSkillResponse.builder()
                .studentId(studentSkill.getStudent().getUserId())
                .skillId(studentSkill.getSkill().getSkillId())
                .level(studentSkill.getLevel())
                .build();
    }
}
