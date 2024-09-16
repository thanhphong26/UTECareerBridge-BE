package com.pn.career.repositories;
import com.pn.career.models.BenefitDetail;
import com.pn.career.models.BenefitDetailId;
import com.pn.career.models.Employer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BenefitDetailRepository extends JpaRepository<BenefitDetail, BenefitDetailId> {
    List<BenefitDetail> findAllByEmployer(Employer employer);
}