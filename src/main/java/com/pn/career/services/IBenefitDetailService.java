package com.pn.career.services;

import com.pn.career.dtos.BenefitDetailDTO;
import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.models.BenefitDetail;
import com.pn.career.models.BenefitDetailId;
import com.pn.career.models.Employer;

import java.util.List;
import java.util.Optional;

public interface IBenefitDetailService {
    Optional<BenefitDetail> updateBenefitDetail(BenefitDetail benefitDetail);
    boolean deleteBenefitDetail(BenefitDetail benefitDetail);
    BenefitDetail findBenefitDetailById(BenefitDetailId benefitDetailId) throws DataNotFoundException;
    void createBenefitDetail(Employer employer, List<BenefitDetailDTO> benefitDetailDTOs);
    List<BenefitDetail> findAllByEmployerId(Employer employer);
}
