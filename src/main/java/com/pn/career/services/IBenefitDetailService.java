package com.pn.career.services;

import com.pn.career.dtos.BenefitDetailDTO;
import com.pn.career.models.BenefitDetail;
import com.pn.career.models.BenefitDetailId;
import com.pn.career.models.Employer;

import java.util.List;
import java.util.Optional;

public interface IBenefitDetailService {
    BenefitDetail insertBenefitDetail(BenefitDetail benefitDetail);
    Optional<BenefitDetail> updateBenefitDetail(BenefitDetail benefitDetail);
    boolean deleteBenefitDetail(BenefitDetail benefitDetail);
    Optional<BenefitDetail> findBenefitDetailById(BenefitDetailId benefitDetailId);
    void createBenefitDetail(Employer employer, List<BenefitDetailDTO> benefitDetailDTOs);
    List<BenefitDetail> findAllByEmployerId(Employer employer);
}
