package com.pn.career.repositories;
import com.pn.career.models.Benefit;
import com.pn.career.models.BenefitDetail;
import com.pn.career.models.BenefitDetailId;
import com.pn.career.models.Employer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface BenefitDetailRepository extends JpaRepository<BenefitDetail, BenefitDetailId> {
    List<BenefitDetail> findAllByEmployer(Employer employer);
    List<BenefitDetail> findAllByBenefit(Benefit benefit);
    @Query("DELETE FROM BenefitDetail bd WHERE bd.employer = :employer AND bd.benefit.benefitId IN :benefitIds")
    void deleteByEmployerAndBenefitIdIn(@Param("employer") Employer employer,@Param("benefitIds") Set<Integer> benefitIds);
}