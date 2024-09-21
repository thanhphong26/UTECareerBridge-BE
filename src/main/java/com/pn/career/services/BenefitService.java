package com.pn.career.services;

import com.pn.career.dtos.BenefitDTO;
import com.pn.career.dtos.BenefitUpdateDTO;
import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.exceptions.DuplicateBenefitNameException;
import com.pn.career.models.Benefit;
import com.pn.career.models.BenefitDetail;
import com.pn.career.repositories.BenefitDetailRepository;
import com.pn.career.repositories.BenefitRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class BenefitService implements IBenefitService{
    private final BenefitRepository benefitRepository;
    private final BenefitDetailRepository benefitDetailRepository;
    @Override
    public List<Benefit> findAllBenefits(boolean isAdmin) {
        if (isAdmin) {
            return benefitRepository.findAll();
        }
        return benefitRepository.findAllByIsActiveTrue();
    }

    @Override
    public Benefit createBenefit(BenefitDTO benefit) {
        if(benefitRepository.existsByBenefitNameIgnoreCase(benefit.getBenefitName())){
            throw new DuplicateBenefitNameException("Phúc lợi có tên "+benefit.getBenefitName()+" đã tồn tại");
        }
        Benefit newBenefit=Benefit.builder()
                .benefitName(benefit.getBenefitName())
                .benefitIcon(benefit.getBenefitIcon())
                .isActive(true)
                .build();
        return benefitRepository.save(newBenefit);
    }

    @Override
    public Benefit getBenefitById(Integer benefitId) {
        return benefitRepository.findById(benefitId).orElseThrow(()->new DataNotFoundException("Không tìm thấy phúc lợi có id "+benefitId));
    }

    @Override
    public Benefit updateBenefit(Integer benefitId, BenefitUpdateDTO benefit) {
        Benefit existingBenefit=getBenefitById(benefitId);
        if (!Objects.equals(existingBenefit.getBenefitName(), benefit.getBenefitName())) {
            // Chỉ kiểm tra trùng lặp nếu tên thực sự thay đổi
            if (benefitRepository.existsByBenefitNameIgnoreCase(benefit.getBenefitName())) {
                throw new DuplicateBenefitNameException("Phúc lợi có tên " + benefit.getBenefitName() + " đã tồn tại");
            }
            existingBenefit.setBenefitName(benefit.getBenefitName());
        }
        existingBenefit.setBenefitIcon(benefit.getBenefitIcon());
        existingBenefit.setActive(benefit.isActive());
        return benefitRepository.save(existingBenefit);
    }
    @Override
    public void deleteBenefit(Integer benefitId) throws Exception {
        List<BenefitDetail> benefitDetails=benefitDetailRepository.findAllByBenefit(getBenefitById(benefitId));
        if(!benefitDetails.isEmpty()){
            throw new Exception("Không thể xóa phúc lợi do đang được sử dụng");
        }
        benefitRepository.deleteById(benefitId);
    }
}
