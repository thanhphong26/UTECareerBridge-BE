package com.pn.career.services;

import com.pn.career.models.Skill;
import com.pn.career.repositories.SkillRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@AllArgsConstructor
public class SkillService implements ISkillService{
    private final SkillRepository skillRepository;
    @Override
    public List<Skill> findAllSkills(boolean isAdmin) {
        if(isAdmin){
            return skillRepository.findAll();
        }
        return skillRepository.findAllByIsActiveTrue();
    }
}
