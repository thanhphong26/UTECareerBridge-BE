package com.pn.career.services;

import com.pn.career.dtos.BenefitDTO;
import com.pn.career.dtos.BenefitUpdateDTO;
import com.pn.career.models.Benefit;
import com.pn.career.models.BenefitDetail;

import java.util.List;

public interface IBenefitService {
    List<Benefit> findAllBenefits(boolean isAdmin);
    Benefit createBenefit(BenefitDTO benefit);
    Benefit getBenefitById(Integer benefitId);
    Benefit updateBenefit(Integer benefitId, BenefitUpdateDTO benefit);
    void deleteBenefit(Integer benefitId) throws Exception;
}
