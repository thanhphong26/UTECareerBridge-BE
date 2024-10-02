package com.pn.career.services;

import com.pn.career.dtos.BenefitDetailDTO;
import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.models.*;
import com.pn.career.repositories.BenefitDetailRepository;
import com.pn.career.repositories.BenefitRepository;
import com.pn.career.repositories.EmployerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BenefitDetailService implements IBenefitDetailService{
    private final BenefitDetailRepository benefitDetailRepository;
    private final EmployerRepository employerRepository;
    private final BenefitRepository benefitRepository;
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
    @Transactional
    public void updateBenefitDetail(Employer employer,  List<BenefitDetailDTO> benefitDetailDTOs) {
        Set<BenefitDetailId> updatedIds = new HashSet<>();
        Map<Integer, BenefitDetail> existingBenefits = findAllByEmployerId(employer).stream()
                .collect(Collectors.toMap(bd -> bd.getId().getBenefitId(), bd -> bd));

        for (BenefitDetailDTO dto : benefitDetailDTOs) {
            BenefitDetailId id = new BenefitDetailId(employer.getUserId(), dto.getBenefitId());
            BenefitDetail benefitDetail = existingBenefits.getOrDefault(dto.getBenefitId(), new BenefitDetail());

            updateOrCreateBenefitDetail(benefitDetail, employer, dto);
            updatedIds.add(id);
        }

        // Remove benefit details that are no longer present
        existingBenefits.values().stream()
                .filter(bd -> !updatedIds.contains(bd.getId()))
                .forEach(benefitDetailRepository::delete);
    }
    @Override
    @Transactional
    public void updateOrCreateBenefitDetail(BenefitDetail benefitDetail, Employer employer, BenefitDetailDTO dto) {
        Benefit benefit = benefitRepository.findById(dto.getBenefitId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin phúc lợi phù hợp"));

        benefitDetail.setId(new BenefitDetailId(employer.getUserId(), dto.getBenefitId()));
        benefitDetail.setEmployer(employer);
        benefitDetail.setBenefit(benefit);
        benefitDetail.setDescription(dto.getDescription());
        benefitDetailRepository.save(benefitDetail);
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
    public List<BenefitDetail> findAllByEmployerId(Employer employer) {
        return benefitDetailRepository.findAllByEmployer(employer);
    }
}
