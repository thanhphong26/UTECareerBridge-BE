package com.pn.career.services;

import com.pn.career.dtos.BenefitDetailDTO;
import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.models.Benefit;
import com.pn.career.models.BenefitDetail;
import com.pn.career.models.BenefitDetailId;
import com.pn.career.models.Employer;
import com.pn.career.repositories.BenefitDetailRepository;
import com.pn.career.repositories.BenefitRepository;
import com.pn.career.repositories.EmployerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
@Service
@AllArgsConstructor
public class BenefitDetailService implements IBenefitDetailService{
    private final BenefitDetailRepository benefitDetailRepository;
    private final EmployerRepository employerRepository;
    private final BenefitRepository benefitRepository;

    @Override
    public Optional<BenefitDetail> updateBenefitDetail(BenefitDetail benefitDetail) {
        if(benefitDetailRepository.existsById(benefitDetail.getId())){
            return Optional.of(benefitDetailRepository.save(benefitDetail));
        }
        return Optional.empty();
    }
    @Override
    @Transactional
    public boolean deleteBenefitDetail(BenefitDetail benefitDetail) {
        if(benefitDetailRepository.existsById(benefitDetail.getId())){
            benefitDetailRepository.delete(benefitDetail);
            return true;
        }
        return false;
    }

    @Override
    public BenefitDetail findBenefitDetailById(BenefitDetailId benefitDetailId) throws DataNotFoundException {
        Optional<BenefitDetail> benefitDetail=benefitDetailRepository.findById(benefitDetailId);
        if(benefitDetail.isEmpty()){
            throw new DataNotFoundException("Không tìm thấy thông tin phúc lợi phù hợp");
        }
        return benefitDetail.get();
    }
    @Override
    @Transactional
    public void createBenefitDetail(Employer employer, List<BenefitDetailDTO> benefitDetailDTOs) {
        for(BenefitDetailDTO benefitDetailDTO:benefitDetailDTOs){
            Benefit benefit=benefitRepository.findById(benefitDetailDTO.getBenefitId()).orElseThrow(()->new RuntimeException("Không tìm thấy thông tin phúc lợi phù hợp"));
            BenefitDetail benefitDetail=BenefitDetail.builder()
                    .id(BenefitDetailId.builder()
                            .benefitId(benefitDetailDTO.getBenefitId())
                            .employerId(employer.getUserId())
                            .build())
                    .employer(employer)
                    .benefit(benefit)
                    .description(benefitDetailDTO.getDescription())
                    .build();
            benefitDetailRepository.save(benefitDetail);
        }
    }
    @Override
    public List<BenefitDetail> findAllByEmployerId(Employer employer) {
        return benefitDetailRepository.findAllByEmployer(employer);
    }
}
