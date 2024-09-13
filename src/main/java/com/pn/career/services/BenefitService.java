package com.pn.career.services;

import com.pn.career.models.Benefit;
import com.pn.career.repositories.BenefitRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@AllArgsConstructor
public class BenefitService implements IBenefitService{
    private final BenefitRepository benefitRepository;
    @Override
    public List<Benefit> findAllBenefits(boolean isAdmin) {
        if(isAdmin){
            return benefitRepository.findAll();
        }
        return benefitRepository.findAllByIsActiveTrue();
    }
}
