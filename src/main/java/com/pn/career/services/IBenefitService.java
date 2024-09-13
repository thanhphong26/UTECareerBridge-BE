package com.pn.career.services;

import com.pn.career.models.Benefit;

import java.util.List;

public interface IBenefitService {
    List<Benefit> findAllBenefits(boolean isAdmin);
}
