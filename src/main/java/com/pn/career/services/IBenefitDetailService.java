package com.pn.career.services;

import com.pn.career.dtos.BenefitDetailDTO;
import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.models.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IBenefitDetailService {
    boolean deleteBenefitDetail(BenefitDetail benefitDetail);
    void updateBenefitDetail(Employer employer,  List<BenefitDetailDTO> benefitDetailDTOs);
    void updateOrCreateBenefitDetail(BenefitDetail benefitDetail, Employer employer, BenefitDetailDTO dto);
    BenefitDetail findBenefitDetailById(BenefitDetailId benefitDetailId) throws DataNotFoundException;
    List<BenefitDetail> findAllByEmployerId(Employer employer);
}
