package com.pn.career.services;

import com.pn.career.dtos.SkillDTO;
import com.pn.career.dtos.SkillUpdateDTO;
import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.exceptions.DuplicateBenefitNameException;
import com.pn.career.models.JobSkill;
import com.pn.career.models.Skill;
import com.pn.career.models.StudentSkill;
import com.pn.career.repositories.JobSkillRepository;
import com.pn.career.repositories.SkillRepository;
import com.pn.career.repositories.StudentSkillRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class SkillService implements ISkillService{
    private final SkillRepository skillRepository;
    private final JobSkillRepository jobSkillRepository;
    private final StudentSkillRepository studentSkillRepository;
    @Override
    public List<Skill> findAllSkills(boolean isAdmin) {
        if(isAdmin){
            return skillRepository.findAll();
        }
        return skillRepository.findAllByIsActiveTrue();
    }
    @Override
    @Transactional
    public Skill createSkill(SkillDTO skill) {
        if(skillRepository.existsBySkillName(skill.getSkillName())){
            throw new DuplicateKeyException("Kỹ năng "+skill.getSkillName()+" đã tồn tại");
        }
        Skill skill1=Skill.builder().skillName(skill.getSkillName()).build();
        skill1.setActive(true);
        return skillRepository.save(skill1);
    }
    @Override
    public Skill getSkillById(Integer skillId)  {
        return skillRepository.findById(skillId).orElseThrow(() -> new DataNotFoundException("Không tìm thấy Skill tương ứng"));
    }
    @Override
    @Transactional
    public Skill updateSkill(Integer skillId, SkillUpdateDTO skill) {
        Skill existingSkill=getSkillById(skillId);
        if (!Objects.equals(existingSkill.getSkillName(), skill.getSkillName())) {
            // Chỉ kiểm tra trùng lặp nếu tên thực sự thay đổi
            if (skillRepository.existsBySkillName(skill.getSkillName())) {
                throw new DuplicateBenefitNameException("Phúc lợi có tên " + skill.getSkillName() + " đã tồn tại");
            }
            existingSkill.setSkillName(skill.getSkillName());
        }
        existingSkill.setSkillName(skill.getSkillName());
        existingSkill.setActive(skill.isActive());
        return skillRepository.save(existingSkill);
    }
    @Override
    @Transactional
    public Skill deleteSkill(Integer skillId) throws Exception {
        Skill skill=getSkillById(skillId);
        List<JobSkill> jobSkills=jobSkillRepository.findAllBySkill(skill);
        List<StudentSkill> studentSkills=studentSkillRepository.findAllBySkill(skill);
        if(!jobSkills.isEmpty()||!studentSkills.isEmpty()){
            throw new Exception("Không thể xóa kỹ năng đã được sử dụng");
        }else{
            skillRepository.deleteById(skillId);
            return skill;
        }
    }
}
